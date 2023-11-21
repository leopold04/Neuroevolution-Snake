/**
 * The Snake class represents the snake in the Snake Game.
 * It manages the snake's position, movement, collision detection,
 * and provides methods for handling neural network inputs.
 */
public class Snake {

      int x[] = new int[SnakeGame.GAME_UNITS];
      int y[] = new int[SnakeGame.GAME_UNITS];

    final double gameHeight = SnakeGame.height;

    final double gameWidth = SnakeGame.width;

    final int unit_size = SnakeGame.unit_size;

    int bodyParts = SnakeGame.defaultBodyParts;
    int applesEaten;

    char direction = SnakeGame.defaultDirection;

    boolean alive = true;

    int moves = 0;

    int view = SnakeGame.view;

    Apple apple = new Apple();

    char[] directions = new char[]{'R','L','U','D'};

    NeuralNetwork brain = new NeuralNetwork(SnakeGame.brainArchitecture);

    int fitness = 0;

    double[] inputs;

    double[] outputs;

    /**
     * Constructs a new Snake object. Initializes neural network inputs and outputs.
     * If random starting position is enabled, sets a random initial position;
     * otherwise, sets the position at the center of the game board.
     */
    public Snake(){
        // our inputs are the first layer of our nodes matrix
        // we can just create a reference to that array and edit it here. if porting to another language, just edit brain.nodes[0] directly

        inputs = brain.nodes[0];

        // same with output nodes
        outputs = brain.nodes[brain.nodes.length - 1];

        if (SnakeGame.randomStartingPosition){
            setRandomPosition();
        }
        else{
            setPosition((int)gameWidth / 2, (int)gameHeight / 2);
        }
        direction = directions[(int)(Math.random() * 4)];

    }

    /**
     * Sets the neural network inputs based on the snake's current state.
     */
    public void setInputs(){
        // our inputs are the first layer of our nodes matrix
        // we can just create a reference to that array and edit it here. if porting to another language, just edit brain.nodes[0] directly
        // larger numbers in this case respond to a greater urgency to act

        // Input Scheme 1
        /*
        inputs[0] = 1 - (y[0] / gameHeight);
        inputs[1] = 1 - ((gameHeight - y[0]) / gameHeight);
        inputs[2] = (x[0] / gameWidth);
        inputs[3] = (gameWidth - x[0]) / gameWidth;
        inputs[4] = (1 - (northTail() / gameHeight));
        inputs[5] = (1 - (southTail() / gameHeight));
        inputs[6] = (1 - (westTail() / gameWidth));
        inputs[7] = (1 - (eastTail() / gameWidth));
        inputs[8] = (y[0] - apple.y) / gameHeight;
        inputs[9] = (apple.x - x[0]) / gameWidth;
        inputs[10] = (direction == 'R') ? 1 : 0;
        inputs[11] = (direction == 'L') ? 1 : 0;
        inputs[12] = (direction == 'U') ? 1 : 0;
        inputs[13] = (direction == 'D') ? 1 : 0;

         */

/*

        // Input Scheme 2
        // detecting if the snake is right next to a wall
        inputs[0] =  y[0] == 0 ? 1 : 0;
        inputs[1] = gameHeight - y[0] == unit_size ? 1 : 0;
        inputs[2] = x[0] == 0 ? 1 : 0;
        inputs[3] = gameHeight - x[0] == unit_size ? 1 : 0;
        // detecting if the snake is right next to its own tail
        inputs[4] = northTail() == unit_size ? 1 : 0;
        inputs[5] = southTail() == unit_size ? 1 : 0;
        inputs[6] = westTail() == unit_size ? 1 : 0;
        inputs[7] = eastTail() == unit_size ? 1 : 0;
        // apple left or right and up or down
        inputs[8] = y[0] - apple.y > 0 ? 1 : -1;
        inputs[9] = apple.x - x[0] > 0 ? 1 : -1;
        // direction
      // inputs[10] = (direction == 'R') ? 1 : 0;
       // inputs[11] = (direction == 'L') ? 1 : 0;
        //inputs[12] = (direction == 'U') ? 1 : 0;
       // inputs[13] = (direction == 'D') ? 1 : 0;

/*


// Input Scheme 3
        /*
        inputs[0] = Math.min(northTail(), y[0] + unit_size) == unit_size ? 1 : 0;
        inputs[1] = Math.min(southTail(), gameHeight - y[0]) == unit_size ? 1 : 0;
        inputs[2] = Math.min(westTail(),x[0]) <= unit_size ? 1 : 0;
        inputs[3] = Math.min(eastTail(), gameWidth - x[0]) <= unit_size ? 1 : 0;
        inputs[4] = y[0] - apple.y > 0 ? 1 : -1;
        inputs[5] = apple.x - x[0] > 0 ? 1 : -1;

         */

        /*

        // Input Scheme 4
          inputs[0] = (1 - (Math.min(northTail(),y[0]) / gameHeight));
        inputs[1] = (1 - (Math.min(southTail(),gameHeight - y[0]) / gameHeight));
        inputs[2] = (1 - (Math.min(westTail(),x[0]) / gameWidth));
        inputs[3] = (1 - (Math.min(eastTail(),gameWidth - x[0]) / gameWidth));
        inputs[4] = y[0] - apple.y > 0 ? 1 : -1;
        inputs[5] = apple.x - x[0] > 0 ? 1 : -1;



         */


        // encoded
        // 3x3 box around snake
        // apple xy
        // facing up

        int startX = x[0] - (int) (view / 2) * unit_size;
        int startY = y[0] - (int) (view / 2) * unit_size;
    for (int i = 0; i < view; i++)
    {
        for (int j = 0; j < view; j++)
        {
            inputs[i * view + j] = obstacle(startX + i * unit_size, startY + j * unit_size);
        }
    }
    // apple left/right
    int square = view * view;
    inputs[square] = apple.x > x[0] ? 1 : -1;
    inputs[square + 1] = apple.y < y[0] ? 1 : -1;






    }

    /**
     * Sets the snake's position to the specified coordinates.
     *
     * @param xP The x-coordinate.
     * @param yP The y-coordinate.
     */
    public void setPosition(int xP, int yP)
    {
        for (int i = 0; i < x.length; i++){
            x[i] = xP;
            y[i] = yP;
        }
    }

    public void setRandomPosition()
    {
        // range is 0 to screendimension - unit size so it does not spawn on edge of screen
        int xP = (int)(Math.random() * (gameWidth - 2 *unit_size) + unit_size);
        int yP = (int)(Math.random() * (gameHeight - 2* unit_size) + unit_size);
        for (int i = 0; i < x.length; i++)
        {
            // goes to the nearest number that is a multiple of Unit size so that collisions work
            x[i] = (xP / unit_size) * unit_size;
            y[i] = (yP / unit_size) * unit_size;
        }
    }

    public void move() {
        fitness = (int) (applesEaten * 50 + (0 * distanceToApple()));
      //  fitness = (int) (moves + (Math.pow(2,applesEaten) + Math.pow(applesEaten,2.1) * 500) - (Math.pow(applesEaten,1.2) * Math.pow((0.25 * moves),1.3)));
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        moves++;
        switch (direction) {
            case 'U' -> y[0] = y[0] - SnakeGame.unit_size;
            case 'D' -> y[0] = y[0] + SnakeGame.unit_size;
            case 'L' -> x[0] = x[0] - SnakeGame.unit_size;
            case 'R' -> x[0] = x[0] + SnakeGame.unit_size;
        }
        if (moves > 4000){
            alive = false;
        }

    }

    public double distanceToApple(){
        return Math.sqrt(2) - Math.sqrt(Math.pow((x[0] - apple.x) / gameWidth, 2) + Math.pow((y[0] - apple.y) / gameHeight, 2));
    }

    public void chooseDirection(){
        // outputs = {R, L, U, D}
        // greedy selection (always picking the highest output)
        if (!SnakeGame.probabilisticChoice) {
            double maxNum = 0;
            int maxIdx = 0;
            for (int i = 0; i < outputs.length; i++) {
                if (outputs[i] > maxNum) {
                    maxNum = outputs[i];
                    maxIdx = i;
                }
            }
            direction = directions[maxIdx];
        }
        else{
            double outputSum = 0;
            for (int i = 0; i < outputs.length; i++){
                outputSum += outputs[i];
            }
            double position = Math.random() * outputSum;
            double sum = 0;
            for (int j = 0; j < outputs.length; j++){
                sum += outputs[j];
                if (sum >= position){
                    direction = directions[j];
                    break;
                }
            }

        }

        // probabilistic selection (selecting based off a weighted probability with the output vals)


    }

        public void checkApple()
        {
            if((x[0] == apple.x) && (y[0] == apple.y))
            {
                bodyParts++;
                applesEaten++;
                apple.moveApple();
            }
        }

        public void checkCollisions() {
            //checks if head collides with body
            for(int i = bodyParts;i > 0;i--)
            {
                if ((x[0] == x[i]) && (y[0] == y[i]))
                {

                    alive = false;
                    break;
                }
            }
            //check if head touches left border
            if(x[0] < 0) {
                alive = false;
                x[0] = 0;
            }
            //check if head touches right border
            if(x[0] > SnakeGame.width - unit_size) {
                alive = false;
            }
            //check if head touches top border
            if(y[0] < 0) {
                alive = false;
            }
            //check if head touches bottom border
            if(y[0] > SnakeGame.height - unit_size) {
                alive = false;
            }

    }

    public double northTail()
    {
        for (int i = 0; i < bodyParts; i++)
        {
            if (x[i] == x[0] && y[0] > y[i] && direction != 'D')
            {
                // absolute value distance from closest north tail segment
                return Math.abs(y[0] - y[i]);
                // remember 0,0 is top left corner of screen
            }
        }
        return gameHeight;
    }
    public double southTail(){
        for (int i = 0; i < bodyParts; i++)
        {
            if (x[i] == x[0] && y[0] < y[i] && direction != 'U')
            {
                // absolute value distance from closest south tail segment
                return Math.abs(y[i] - y[0]);
            }
        }
        return gameHeight;

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
        return gameWidth;
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
        return gameWidth;
    }



    // 0 is empty space
    // 1 is any wall/tail
    public int obstacle(int xP, int yP){
        // wall
        if ((xP < 0) || (xP > gameWidth - unit_size) || (yP < 0) || (yP > gameHeight - unit_size)){
            return 1;
        }

        // tail (excluding the head bc we dont wanna count that)
        for (int i = 0; i < bodyParts; i++){
            if (x[i] == xP && y[i] == yP){
                return 1;
            }
        }
    return 0;
    }

}
