import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class Snake
{
    static final int UNIT_SIZE = 25;
    static final int MOVE_LIMIT = 50;
    // x position and y position of body segments
    int x[] = new int[GamePanel.GAME_UNITS];
    int y[] = new int[GamePanel.GAME_UNITS];
    double dWidth = (double)GamePanel.SCREEN_WIDTH;
    double dHeight = (double) GamePanel.SCREEN_HEIGHT;
    char direction = 'L';
    char[] directions = new char[]{'R','L','U','D'};
    char[] turns = new char[]{'R','L','F'};
    int bodyParts = 4;
    boolean alive = true;
    int totalMoves = 0;
    int allowedMovesTaken = 0;
    int fitness = 0;
    int iD = 0;
    int applesEaten;
    NeuralNetwork brain;
    int numOut;
    double[] chromosome;
    Apple myApple;


    public Snake()
    {
        applesEaten = 0;
        brain = new NeuralNetwork();
        chromosome = createChromosome();
        myApple = new Apple();
        myApple.moveApple();
        numOut = brain.numOutputs;
    }

    public Snake copy(Snake copy)
    {
        applesEaten = 0;
        brain = new NeuralNetwork();
        chromosome = Arrays.copyOf(copy.chromosome,copy.chromosome.length);
        myApple = copy.myApple;
        myApple.moveApple();
        numOut = brain.numOutputs;
        return copy;
    }



    public void setPosition(int xP, int yP)
    {
        for (int i = 0; i < x.length; i++)
        {
            // goes to nearest number that is a multiple of Unit size so that collisions work
            x[i] = (xP / UNIT_SIZE) * UNIT_SIZE;
            y[i] = (yP / UNIT_SIZE) * UNIT_SIZE;
        }
    }

    public void setRandomPosition()
    {
        // range is 0 to screendimension - unit size so it does not spawn on edge of screen
        int xP = (int)(Math.random() * (GamePanel.SCREEN_WIDTH - UNIT_SIZE - UNIT_SIZE) + UNIT_SIZE);
        int yP = (int)(Math.random() * (GamePanel.SCREEN_HEIGHT - UNIT_SIZE - UNIT_SIZE) + UNIT_SIZE);
        for (int i = 0; i < x.length; i++)
        {
            // goes to nearest number that is a multiple of Unit size so that collisions work
            x[i] = (xP / UNIT_SIZE) * UNIT_SIZE;
            y[i] = (yP / UNIT_SIZE) * UNIT_SIZE;
        }
    }


    public void setInputs()
    {
        // Smaller distances generally produce higher numbers
        // Distance to Walls (North, South, East, West)
        // Distance to Tail (North, South, West, East)
        // Distance to Apple (Vertical, Horizontal)
        // Current Direction (Right, Left, Up, Down) - One Hot Encoded
        // Size of snake
        // Euclidean Distance to Apple
        brain.inputs[0] = 1 - (y[0] / dHeight);
        brain.inputs[1] = 1 - ((GamePanel.SCREEN_HEIGHT - y[0]) / dHeight);
        brain.inputs[2] = (x[0] / dWidth);
        brain.inputs[3] = (GamePanel.SCREEN_WIDTH - x[0]) / dWidth;
        brain.inputs[4] = 4 * (northTail() / dHeight);
        brain.inputs[5] = 4 * (southTail() / dHeight);
        brain.inputs[6] = 4 * (westTail() / dWidth);
        brain.inputs[7] = 4 * (eastTail() / dWidth);
        brain.inputs[8] = 2 * (y[0] - myApple.appleY) / dHeight;
        brain.inputs[9] = 2 * (myApple.appleX - x[0]) / dWidth;
        brain.inputs[10] = ifDirection('R');
        brain.inputs[11] = ifDirection('L');
        brain.inputs[12] = ifDirection('U');
        brain.inputs[13] = ifDirection('D');
        brain.inputs[14] = (bodyParts * UNIT_SIZE) / (dHeight * dWidth);
        brain.inputs[15] = (Math.sqrt(Math.pow((myApple.appleX - x[0]),2) + Math.pow(y[0] - myApple.appleY,2)) / dHeight);
    }


    public void chooseDirection()
    {
        char outcome = 'X';
        int bestOutputIdx = 0;
        double bestOutput = 0;
        for (int i = 0; i < brain.outputs.length; i++)
        {
            if (brain.outputs[i] > bestOutput)
            {
                bestOutput = brain.outputs[i];
                bestOutputIdx = i;
            }
        }
        if (numOut == 4)
        {
            direction = directions[bestOutputIdx];
        }
        else
        {
            outcome = turns[bestOutputIdx];
            for (int j = 0; j < brain.outputs.length; j++){
                if (j != bestOutputIdx){
                    brain.outputs[j] = 0;
                }
            }
            if (outcome == 'R'){
                direction = clockWise(direction);
            }
            if (outcome == 'L'){
                direction = counterClockWise(direction);
            }
        }

    }

    // unused method to choose a direction using roulette wheel selection
    public void chooseDirectionProbabilistically()
    {
        double sum = 0;
        char outcome = 'X';
        double[] probabilities = Arrays.copyOf(brain.outputs,brain.outputs.length);
        for (int i = 0; i < brain.outputs.length; i++)
        {
            sum += brain.outputs[i];
        }

        for (int k = 0; k < probabilities.length; k++)
        {
            probabilities[k] = probabilities[k] / sum;
            //    System.out.println(directions[k] + ":  " + probabilities[k] + "  ");
        }

        double randNum = Math.random();
        int idx = 0;
        while (randNum > 0){
            randNum = randNum - probabilities[idx];
            idx++;
        }
        // because idx++ before exiting the loop
        if (numOut == 3)
        {
            outcome = turns[idx - 1];
            for (int j = 0; j < turns.length; j++){
                if (j != idx - 1){
                    brain.outputs[j] = 0;
                }
            }
            if (outcome == 'R'){
                direction = clockWise(direction);
            }
            if (outcome == 'L'){
                direction = counterClockWise(direction);
            }
        }
        else
        {
            direction = directions[idx - 1];
        }

    }


    // combines 2d arrays of weights to a single 1d array
    public double[] createChromosome()
    {
        // weights of the brain of the snake
        double[] chromosomePart1 = new double[brain.weightsInput.length * brain.weightsInput[0].length];
        for(int i = 0; i < brain.weightsInput.length; i++)
        {
            for(int k = 0; k < brain.weightsInput[i].length; k++)
            {
                //System.out.println(brain.weightsInput[i][k]);
                // System.out.println(i * (brain.weightsInput[i].length) + k);
                chromosomePart1[i * (brain.weightsInput[i].length) + k] = brain.weightsInput[i][k];
            }
        }
        double[] chromosomePart2 = new double[brain.weightsHidden.length * brain.weightsHidden[0].length];
        for(int j = 0; j < brain.weightsHidden.length; j++)
        {
            for (int m = 0; m < brain.weightsHidden[j].length; m++){
                //   System.out.println(brain.weightsInput[j][m]);
                chromosomePart2[j * (brain.weightsHidden[j].length) + m] = brain.weightsHidden[j][m];

            }
        }
        double[] fullchromosome = Arrays.copyOf(chromosomePart1, chromosomePart1.length + chromosomePart2.length);
        System.arraycopy(chromosomePart2, 0, fullchromosome, chromosomePart1.length, chromosomePart2.length);
        return fullchromosome;
    }

    public String exportChromosome()
    {
        String chromString = "";
        for (int p = 0; p < chromosome.length; p++)
        {
            chromString += String.valueOf(chromosome[p]) + " ";
        }
        return chromString;
    }

    public void importChromosome(String chromosome)
    {
        // input weights
        String[] chrom1 = chromosome.split(" ",brain.weightsInput.length * brain.weightsInput[0].length + 1);

        for(int i = 0; i < brain.weightsInput.length; i++)
        {
            for (int k = 0; k < brain.weightsInput[i].length; k++)
            {
                brain.weightsInput[i][k] = Double.parseDouble(chrom1[i * brain.weightsInput[i].length + k]);
            }
        }
        // total array of weights (input + hidden)
        String[] partChrom = chromosome.split(" ",chrom1.length + brain.weightsHidden.length * brain.weightsHidden[0].length + 1);

        // hidden weights
        String[] chrom2 = Arrays.copyOfRange(partChrom,brain.weightsInput.length * brain.weightsInput[0].length, partChrom.length);
        for (int j = 0; j < brain.weightsHidden.length; j++)
        {
            for (int m = 0; m < brain.weightsHidden[j].length; m++)
            {
                //    System.out.println(chrom2[j * brain.weightsHidden[j].length + m]);
                brain.weightsHidden[j][m] = Double.parseDouble(chrom2[j * brain.weightsHidden[j].length + m]);
            }
        }
    }

    public void assignGenes()
    {
        importChromosome(exportChromosome());
    }

    public void printOutputs(){
        for (int i = 0; i < brain.outputs.length; i++){
            System.out.println(directions[i] + ":  " + brain.outputs[i] + "  ");
        }
        System.out.println();
    }

    public void printWeights()
    {
        System.out.println("Weights Input: ");
        for (int i = 0; i < brain.weightsInput.length; i++)
        {
            for (int k = 0; k < brain.weightsInput[i].length; k++)
            {
                System.out.print(brain.weightsInput[i][k] + " ");
            }
            System.out.println();
        }
        System.out.println("Weights Hidden");
        for (int j = 0; j < brain.weightsHidden.length; j++)
        {
            for (int m = 0; m < brain.weightsHidden[m].length; m++)
            {
                System.out.print(brain.weightsHidden[j][m] + " ");
            }
            System.out.println();
        }
    }

    public double ifDirection(char dir)
    {
        if (direction == dir)
        {
            return 1;
        }
        return 0;
    }

    public double northTail()
    {
        for (int i = 0; i < bodyParts; i++)
        {
            if (x[i] == x[0] && y[0] > y[i] && direction != 'D')
            {
                // absolute value distance from closest north tail segment
                return y[0] - y[i];
                // remember 0,0 is top left corner of screen
            }
        }
        return 1;
    }
    public double southTail(){
        for (int i = 0; i < bodyParts; i++)
        {
            if (x[i] == x[0] && y[0] < y[i] && direction != 'U')
            {
                // absolute value distance from closest south tail segment
                return y[i] - y[0];
            }
        }
        return 1;

    }
    public double eastTail(){
        for (int i = 0; i < bodyParts; i++)
        {
            if (y[i] == y[0] && x[i] > x[0] && direction != 'L')
            {
                // absolute value distance from closest east tail segment
                return x[i] - x[0];
            }
        }
        return 1;
    }
    public double westTail(){
        for (int i = 0; i < bodyParts; i++)
        {
            if (y[i] == y[0] && x[i] < x[0] && direction != 'R')
            {
                // absolute value distance from closest west tail segment
                return x[0] - x[i];
            }
        }
        return 1;
    }

    public char counterClockWise(char dir)
    {
        if (dir == 'U') {
            return 'L';
        }
        if (dir == 'L') {
            return 'D';
        }
        if (dir == 'D') {
            return 'R';
        }
        if (dir == 'R') {
            return 'U';
        }
        return 'X';
    }
    public char clockWise(char dir)
    {
        if (dir == 'U'){
            return 'R';
        }
        if (dir == 'R'){
            return 'D';
        }
        if (dir == 'D'){
            return 'L';
        }
        if (dir == 'L'){
            return 'U';
        }
        return 'X';
    }


    public void checkCollisions()
    {
        //checks if head collides with body
        for(int i = bodyParts;i>0;i--) {
            if((x[0] == x[i])&& (y[0] == y[i]))
            {
                alive = false;
            }
        }
        //check if head touches left border
        if(x[0] < 0) {
           // System.out.println("Left");
            alive = false;

        }
        //check if head touches right border
        if(x[0] > GamePanel.SCREEN_WIDTH - UNIT_SIZE) {
       //     System.out.println("right");
            alive = false;

        }
        //check if head touches top border
        if(y[0] < 0) {
        //    System.out.println("Top");
            alive = false;

        }
        //check if head touches bottom border
        if(y[0] > GamePanel.SCREEN_HEIGHT - UNIT_SIZE) {
         //   System.out.println("Floor");
            alive = false;
        }
        if (allowedMovesTaken > MOVE_LIMIT && !SnakeGame.PLAYER_CONTROL)
        {
         //   System.out.println("Move");
            alive = false;

        }
    }

    public void move()
    {
        allowedMovesTaken++;
        totalMoves++;
        // fitness is a function of apples eaten and distance to the next apple
        fitness = ((applesEaten * 3 * MOVE_LIMIT) + (int)(MOVE_LIMIT * (1 - brain.inputs[15]))) / 3;

        for(int i = bodyParts;i>0;i--)
        {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction)
        {
            case 'U':
                y[0] = y[0] - Snake.UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + Snake.UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - Snake.UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + Snake.UNIT_SIZE;
                break;
        }
    }

}

