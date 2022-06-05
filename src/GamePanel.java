// single
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    public static final int SCREEN_WIDTH = (500 / Snake.UNIT_SIZE) * Snake.UNIT_SIZE;
    public static final int SCREEN_HEIGHT = (550/ Snake.UNIT_SIZE) * Snake.UNIT_SIZE;
    public static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(Snake.UNIT_SIZE*Snake.UNIT_SIZE);
    public static final DecimalFormat df = new DecimalFormat("0.00");
    public static int DELAY = 50;
    public static final Color SKY_BLUE = new Color(175, 175, 251 );
    public static final Color NAVY = new Color(51, 70, 122);
    public static final Color BEIGE = new Color(242, 237, 206);
    public static final Color GREEN = new Color(61, 145, 75);
    public int snakeIdx;
    public int generationNum;
    public int populationSize;
    double startTime;
    public String[] testChromosomes;
    // for reference and for visual stuff
    Snake snake = new Snake();
    Snake bestSnake = new Snake();
    public static Snake[] mySnakes;
    public int bestFitness = 0;
    public JSlider speedSlider = new JSlider(1,100,55);
    GeneticAlgorithm GA = new GeneticAlgorithm();
    String testChromosome;
    boolean running = false;
    static Timer timer;
    Random random;

    GamePanel()
    {
        if (SnakeGame.VISUALS_ENABLED)
        {
            // providing extra room for network visualization
            this.setPreferredSize(new Dimension(SCREEN_WIDTH + 370,SCREEN_HEIGHT + 80));
            speedSlider.setPreferredSize(new Dimension(100,50));
            // this might mess with some things
            this.setLayout(null);
            speedSlider.setInverted(true);
            speedSlider.setFocusable(false);
            speedSlider.setPaintTicks(true);
            speedSlider.setMinorTickSpacing(10);
            speedSlider.setPaintLabels(true);
            speedSlider.setBounds(340,560,150,70);
            Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
            table.put(100, new JLabel("Slow"));
            table.put(2, new JLabel("Fast"));
            speedSlider.setLabelTable(table);
            this.add(speedSlider);
            //  this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
            this.setBackground(BEIGE);
            this.setFocusable(true);
            this.addKeyListener(new MyKeyAdapter());
        }
        startGame();
        if (!SnakeGame.VISUALS_ENABLED)
        {
            timer.setDelay(0);
        }
    }
    public void startGame()
    {
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
        populationSize = 1000;
        mySnakes = GA.generatePopulation(populationSize);
        generationNum = 1;
        snakeIdx = 0;
        startTime = System.currentTimeMillis();
        if (SnakeGame.TESTING_MODE)
        {
            // current best chromosome, set to collect around 41 apples
            // other chromosomes can be found in text file 'Chromosomes'
            testChromosome = "0.8114073974060978 0.5006651314520241 -0.7949776941685034 -1.8182724926816598 -0.2360141597794465 0.2759092234373255 -0.7577750441126043 -0.8556016422478416 -0.19466960208491635 -1.3544747400727952 1.6992046659156386 0.14857121505437698 -0.5998384001754236 0.5357848068342377 0.9380365681397853 -1.055831192279728 -1.302816488705183 -1.9815155674334135 -1.1446270875812954 -0.3172280772854461 -1.512767169300751 0.3534472418105188 0.9842428802730367 -1.2287785062446726 -0.679189641326972 -0.023926985442030535 -1.530825453415337 1.8080313095746394 -0.10954091928703402 -1.9446809943348775 0.052100368660765284 -0.7648732848076882 0.6931142820992049 -1.2859009414382658 -1.4385482338774382 -1.0082541980770698 -0.7930352625222103 -0.6485010069704118 0.42257777619274073 1.4207273286827022 -0.13418152570321817 1.8744407464691464 0.9564908318471912 0.16712162316119228 1.3766806434209538 -1.8200291956769394 -1.8425093276681248 0.30605663877946876 -1.428300346613974 -0.09513408259092104 -0.5421596061734366 0.5790049280740326 -1.5189900661048008 0.21226562141158967 1.8912355136586885 1.042867205716691 0.18682631813572392 -0.7500876371005099 -0.9107753301679979 1.33461293882171 0.03179810262136895 -1.8730589250967427 1.7760911334720073 -0.16433017168207043 1.6515013056295653 -0.08724811636735863 -1.4427844126439604 -1.106401671191223 0.4638456250345073 -1.234938983030204 1.3701582558716097 -0.656202760419105 -0.6464479320868377 -1.0009648939819247 0.7155941632302656 0.9261058045519017 -0.2273925617626693 0.9270066299152222 0.1610799950410935 1.6090155310054772 1.3778492016674484 1.840630867340113 -1.4061782613919394 -0.9320296507394632 -1.4797208698035376 -1.7005164939505337 0.8683490171878447 -1.073684226021943 -1.5939356061454082 0.769329942642301 0.9855086959012147 1.1634104794481037 -0.7311256977266734 -1.2364583057796468 1.934533326110477 1.8113858625938688 -0.6446975926451275 -1.1020905883245247 0.6275031165424627 1.9882138119406703 -0.39011371385942084 -1.276251980034386 0.4584274879076018 -1.6033068000031503 -1.5138067263805257 0.8183200434793649 0.9059091697744361 -1.9256251383918093 0.4031332475668905 1.005382727179975 0.6785798143250674 0.5783075010432643 -0.3556332312985142 0.14634035460639927 -1.7253822651569828 0.7008990017503165 0.09290637170194405 0.9130210722115732 -0.6948952581687227 -0.9047059578389618 0.26058765625592395 -0.7134933752161583 -0.8103423287173457 1.6472256996209769 0.008920126178790344 -0.6431818285187352 0.233196979638723 0.7385216367722003 -0.35812981854643455 -0.0843131280107281 0.181026531981904 0.6196734855643928 0.17890602981148396 -1.1852271122745255 0.08807644321608121 0.29617852440658554 -1.3775648091549306 1.3436110738042895 0.4434432493540168 -0.653725006536023 -1.5019588825283137 0.10430414093502716 -0.3987799303180277 -0.7799046212902687 0.32861738654850603 0.7474845706361268 -0.5787602488296244 0.9600328470143782 1.9984279637147662 0.9256636367916147 1.0601966187588392 1.8211350885803572 0.6167767204049541 0.38836118478500703 1.6391132666844226 -1.4474625511785186 0.7234733128719166 0.3150715831548707 0.9642630363652049 1.00138925233423 -1.2798621191045738 1.4100743476096902 -0.5695976090170292 -0.3229594521407275 -1.8867204930550727 1.396887640674115 0.8190772913049407 -1.520959844718413 -0.722541872505055 -1.6409820144612355 -0.8415135614726248 1.3374761839723912 -0.24749468546030906 0.12522622866559718 -0.5609747426875726 -1.1418765772399877 1.7588499509253719 -0.12489544636446492 -0.4064250855092366 0.15999407779311836 1.8606754603715547 -1.9347689766062541 0.9851289293717187 0.628229945566408 1.0027050744471722 -1.209419713825851 1.6474092460787775 -0.2846179876449395 -0.2084885342390299 1.4117838158957863 -1.637956777106298 1.456012625772531 0.33722751898241254 -0.8532087847165379 -0.46396002909933154 -0.036986007542157306 1.7110695614874074 -1.5267453179832482 1.5009375529586175 -1.0685988816784748";
            for (int i = 0; i < mySnakes.length; i++)
            {
                mySnakes[i].importChromosome(testChromosome);
                mySnakes[i].chromosome = mySnakes[i].createChromosome();
            }
        }

        bestSnake.totalMoves = Integer.MAX_VALUE;
    }


    public void paintComponent(Graphics g)
    {
        if (SnakeGame.VISUALS_ENABLED)
        {
            super.paintComponent(g);
            draw(g);
        }
    }
    public void draw(Graphics g)
    {
        // draws game background
        g.setColor(NAVY);
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        if(running)
        {
            timer.setDelay(speedSlider.getValue());
            for(int i = 0; i< mySnakes[snakeIdx].bodyParts;i++)
            {
                // head color
                if(i == 0)
                {
                    g.setColor(GREEN);
                    g.fillRect(mySnakes[snakeIdx].x[i], mySnakes[snakeIdx].y[i], Snake.UNIT_SIZE, Snake.UNIT_SIZE);
                }
                else {
                    // body color
                    g.setColor(new Color(58, 210, 169));
                  //  g.setColor(Color.WHITE);
                    g.fillRect(mySnakes[snakeIdx].x[i], mySnakes[snakeIdx].y[i], Snake.UNIT_SIZE, Snake.UNIT_SIZE);
                }
            }

            Graphics2D g2 = (Graphics2D) g;
            drawInputs(g);
            drawWeights((Graphics2D) g);
            drawInputNodes((Graphics2D) g);
            drawHiddenNodes((Graphics2D) g);
            drawOutputs((Graphics2D) g);

            g.setColor(new Color (189, 57, 70));
            g.fillOval(mySnakes[snakeIdx].myApple.appleX, mySnakes[snakeIdx].myApple.appleY, Snake.UNIT_SIZE, Snake.UNIT_SIZE);
            g.setFont( new Font("Serif",Font.PLAIN, 15));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.setColor(Color.black);
            g.drawString("Generation: " + generationNum, 2, SCREEN_HEIGHT + 20);
            g.drawString("Individual: " + snakeIdx + " / " + mySnakes.length,2, SCREEN_HEIGHT + 40);
            g.drawString("Best Fitness: " + bestFitness,2, SCREEN_HEIGHT + 60);
            g.drawString("Simulation Speed" ,360, 560);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        if(running)
        {
            mySnakes[snakeIdx].setInputs();
            mySnakes[snakeIdx].brain.forwardPropagation();
            if (!SnakeGame.PLAYER_CONTROL)
            {
                mySnakes[snakeIdx].chooseDirection();
            }
            mySnakes[snakeIdx].move();
            checkApple();
            mySnakes[snakeIdx].checkCollisions();
            if (mySnakes[snakeIdx].fitness > bestFitness)
            {
                bestFitness = mySnakes[snakeIdx].fitness;
            }
            if (!mySnakes[snakeIdx].alive)
            {
                if (!SnakeGame.PLAYER_CONTROL)
                {
                    snakeIdx++;
                    if (snakeIdx == populationSize - 1)
                    {
                        if (GA.sortSnakes(mySnakes)[0].fitness > bestSnake.fitness)
                        {
                            // elapsed time in minutes
                            double elapsedTime = (System.currentTimeMillis() - startTime) / 60000;
                            System.out.println("Generation: " + generationNum + " Apples: " +  GA.sortSnakes(mySnakes)[0].applesEaten + " Fitness: "+ GA.sortSnakes(mySnakes)[0].fitness + " Moves: " + GA.sortSnakes(mySnakes)[0].totalMoves +  " Time (Minutes): " + df.format(elapsedTime) +  " Chromosome:  " + GA.sortSnakes(mySnakes)[0].exportChromosome());
                            bestSnake = new Snake();
                            bestSnake.importChromosome(GA.sortSnakes(mySnakes)[0].exportChromosome());
                            bestSnake.fitness = GA.sortSnakes(mySnakes)[0].fitness;
                            bestSnake.totalMoves = GA.sortSnakes(mySnakes)[0].totalMoves;
                            bestSnake.chromosome = bestSnake.createChromosome();
                        }

                        mySnakes = GA.createNextGeneration(mySnakes,200,820,50,5);
                        generationNum++;
                        snakeIdx = 0;
                    }

                }
                // player control = true
                else
                {
                    mySnakes[snakeIdx] = new Snake();
                    mySnakes[snakeIdx].setPosition(250,270);
                    mySnakes[snakeIdx].setRandomPosition();
                }
            }
        }
        repaint();
    }

    public void drawInputs(Graphics g)
    {
        int sX = 540;
        int sY = 37;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.PLAIN, 10));
        String[] snakeInputs = new String[]{"North Wall", "South Wall", "East Wall","West Wall", "North Tail", "South Tail", "East Tail", "West Tail", "Apple Y", "Apple X", "R","L","U","D","Size", "Apple" };
        for(int i = 0; i < mySnakes[snakeIdx].brain.inputs.length; i++)
        {
            //g.drawString(String.valueOf(snake.brain.inputs[i]), sX, sY + (35 * i));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString(snakeInputs[i],sX - metrics1.stringWidth(snakeInputs[i]) + 10 , sY + (38 * i));
        }
    }

    public void drawInputNodes(Graphics2D g)
    {
        int sX = 555;
        int sY = 20;
        g.setColor(Color.BLACK);
        for(int i = 0; i < mySnakes[snakeIdx].brain.inputs.length; i++)
        {
            g.setColor(Color.white);
            g.fillOval(sX, sY + (38 * i), 25, 25);
            int a = (int)(Math.abs(mySnakes[snakeIdx].brain.inputs[i] * 255));
            if (a > 255)
            {
                a = 255;
            }
            g.setColor(new Color(2, 19, 46,a));
            g.fillOval(sX, sY + (38 * i), 25, 25);

        }
    }

    public void drawHiddenNodes(Graphics2D g)
    {
        int sX = 670;
        int sY = 85;
        g.setColor(Color.BLACK);
        for(int i = 0; i < mySnakes[snakeIdx].brain.hiddenNodes.length; i++)
        {
            g.setColor(Color.white);
            g.fillOval(sX, sY + (50 * i), 25, 25);
            g.setColor(new Color(2, 19, 46,(int)(Math.abs(mySnakes[snakeIdx].brain.hiddenNodes[i]) * 255/2)));
            g.fillOval(sX, sY + (50 * i), 25, 25);
        }
    }

    public void drawWeights(Graphics2D g){
        // input to hidden
        int sX = 570;
        int sY = 30;
        g.setColor(Color.BLACK);
        Color[][] weightInputColors = setWeightColors(mySnakes[snakeIdx].brain.weightsInput);
        for (int i = 0; i < mySnakes[snakeIdx].brain.weightsInput.length; i++)
        {
            for (int k = 0; k < mySnakes[snakeIdx].brain.weightsInput[i].length; k++)
            {
                g.setColor(weightInputColors[i][k]);
                g.drawLine(sX, sY + (38 * i),688, 97 + (50 * k));
            }
        }

        // hidden to output
        Color[][] weightHiddenColors = setWeightColors(mySnakes[snakeIdx].brain.weightsHidden);

        for (int j = 0; j < mySnakes[snakeIdx].brain.weightsHidden.length; j++){
            for (int m = 0; m < mySnakes[snakeIdx].brain.weightsHidden[j].length; m++){
                g.setColor(weightHiddenColors[j][m]);
                g.drawLine(675, 97 + (50 * j),820, 212 + (50 * m));
            }
        }
    }

    public Color[][] setWeightColors(double[][] weights)
    {
        Color[][] weightColors = new Color[weights.length][weights[0].length];
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[0].length; j++)
            {
                Color weightColor;
                if (weights[i][j] >= 0)
                {
                    // blue
                    weightColor = new Color(26,93,201, (int)(Math.abs(weights[i][j] * 255/2)));
                }
                else
                {
                    // red
                    weightColor = new Color(201,26,26, (int)(Math.abs(weights[i][j] * 255/2)));
                }
                weightColors[i][j] = weightColor;
            }
        }
        return weightColors;
    }


    public void drawOutputs(Graphics2D g)
    {
        int sX = 800;
        int sY = 200;
        g.setColor(Color.BLACK);

        for (int i = 0; i < mySnakes[snakeIdx].brain.outputs.length; i++)
        {
            g.setColor(Color.white);
            g.fillOval(sX, sY + (50 * i), 25, 25);
            g.setColor(new Color(2, 19, 46,(int)(Math.abs(mySnakes[snakeIdx].brain.outputs[i]) * 255/2)));
            g.fillOval(sX, sY + (50 * i),25,25);
            g.setColor(Color.black);
            g.drawString(String.valueOf(mySnakes[snakeIdx].directions[i]), sX + 35, sY + 15 + (50 * i));
        }

    }

    public void checkApple()
    {
        if((mySnakes[snakeIdx].x[0] == mySnakes[snakeIdx].myApple.appleX) && (mySnakes[snakeIdx].y[0] == mySnakes[snakeIdx].myApple.appleY))
        {
            mySnakes[snakeIdx].bodyParts++;
            mySnakes[snakeIdx].applesEaten++;
            // resets move count
            mySnakes[snakeIdx].allowedMovesTaken = 0;
            mySnakes[snakeIdx].myApple.moveApple();
        }
    }

    public void gameOver(Graphics g)
    {
        //Score
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 20));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+ mySnakes[snakeIdx].applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+ mySnakes[snakeIdx].applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }



    public class MyKeyAdapter extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(mySnakes[snakeIdx].direction != 'R') {
                        mySnakes[snakeIdx].direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(mySnakes[snakeIdx].direction != 'L') {
                        mySnakes[snakeIdx].direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(mySnakes[snakeIdx].direction != 'D') {
                        mySnakes[snakeIdx].direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(mySnakes[snakeIdx].direction != 'U') {
                        mySnakes[snakeIdx].direction = 'D';
                    }
                    break;
            }
        }
    }
}










