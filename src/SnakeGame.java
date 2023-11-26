import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
public class SnakeGame {
    // graphical
    public static  int width = 500;
    public static  int height = 500;
    public static final int widthBuffer = 700;
    public static final int heightBuffer = 150;
    public static final int displayDelay = 50;

    public static final boolean userControl = false;


    // snake settings
    public static int defaultBodyParts = 4;

    public static char defaultDirection = 'R';
    public static final int unit_size = 25;

    public static boolean randomStartingPosition = true;
    public static final int GAME_UNITS = (width*height)/(unit_size*unit_size);

    public static final int INPUT_SCHEME = 1;

    /** binds the input scheme to the number of input nodes for that scheme */
    public static final Map<Integer, Integer> bindings = new HashMap<Integer, Integer>() {{
        put(1, 14);
        put(2, 10);
        put(3, 6);
        put(4,6);
    }};

    /**list containing the number of nodes in each layer (including input and output layers) */
    public static final int[] brainArchitecture = new int[]{bindings.get(INPUT_SCHEME),4};

    public static final int[] weightRange = new int[]{-4,4};

    public static final boolean randomApple = true;

    public static final boolean visualsEnabled = false;

    public static final int view = 4;
    public static boolean testingMode = false;


    public static final String testWeights ="[18, 4][1.0011797534121474, 3.9621084886293207, 3.826860266600198, 3.485202598759492, 3.5786087597506873, -1.6246329899090455, -1.0596746063069062, 0.5573112302712913, 2.937717145320266, -0.7604677363677679, 1.5086894099925505, -0.09917544520295252, -3.262967375499639, 2.6701006445989206, 1.685241188729508, -2.8548126018709032, 2.9900031745736726, -0.6258890573293723, 3.815245208237342, 1.6382631851941687, 0.22126972774238496, -1.7013383772270387, -1.69821260727886, 1.8406503762242812, 3.0949958200764254, -3.310898151737378, 3.3491592048388243, 0.01745906139750275, 1.3444954608272282, -0.6894005479733964, -0.45015102741879875, -3.1404180768832486, 0.042883865275999966, 0.18193829567918396, -2.7966022107067436, -0.5918233900309504, 2.5645695608755323, -2.0220126031639927, -3.5936861856314275, 2.137010174178913, 1.833674759872383, -2.447784417562109, 0.7485898328795848, 1.4523231192140171, 0.2822590540720826, 2.882057005326674, 0.3716841181173409, -3.8876773517630303, 2.6904656711970487, 0.8162114535598564, 0.6616166250689854, 0.31820488103891353, -3.6861408986517104, 3.8569308038670576, -0.9324681774788006, 1.245534991274309, -3.3138156934896097, 1.9937446349044103, -0.7026480036499478, 2.273918680429234, 0.6298343482618698, 3.0030636981353593, 3.4746279990361053, 2.59609044944626, 2.7974049916490156, -3.5408270886344546, 3.6705189270275698, -3.671951626961312, -3.8522899730761138, 1.3364722688986932, 3.856283164939808, -1.4759661195869]";
    public static int numTesters = 15;
    public static Snake[] mySnakes;
    public static int index = 0;
    static int bestFitness = 0;
    static int generationLimit = 5000;


    // NN Settings


    // GA settings
    public static int generation = 0;
    public static int populationSize = 500;

    public static int numParents = 50;

    public static int numChildren = 400;


    public static double crossoverRate = 0.5;

    public static int crossoverType = 0;

    public static boolean probabilisticParentSelection = false;
    public static boolean probabilisticChoice = false;





    public static void main(String[] args) throws InterruptedException {
        // create initial population
        mySnakes = GeneticAlgorithm.createPopulation(populationSize);

        // allow testing mode
        if (testingMode){
            for (int i = 0; i < numTesters; i++){
                mySnakes[i].brain.importWeightVector(testWeights);
            }
        }


        if (visualsEnabled){
            GameFrame display = new GameFrame();
            for (int i = 0; i < mySnakes.length; i++){
                index = i;
                while (mySnakes[i].alive){
                    Thread.sleep(displayDelay);
                    mySnakes[i].setInputs();
                    mySnakes[i].brain.forwardPropagation();
                    if (!userControl) {
                        mySnakes[i].chooseDirection();
                    }
                    mySnakes[i].move();
                    mySnakes[i].checkApple();
                    mySnakes[i].checkCollisions();
                }
            }

        }
        else {
            for (int g = 0; g < generationLimit; g++) {
                if (generation % 10 == 0){
                    probabilisticChoice = true;
                }else{
                    probabilisticChoice = false;
                }
                generation += 1;
                for (Snake snake : mySnakes) {
                    while (snake.alive) {
                        snake.setInputs();
                        snake.brain.forwardPropagation();
                        snake.chooseDirection();
                        snake.move();
                        snake.checkApple();
                        snake.checkCollisions();
                    }
                    if (snake.fitness > bestFitness) {
                          bestFitness = snake.fitness;
                          System.out.println("Generation: " + generation + " | " + "Fitness: " + bestFitness + " | " + "Apples: " + snake.applesEaten + " | " + "Moves: " + snake.moves);
                          System.out.println(snake.brain.exportWeightVector());
                          System.out.println();

                    }
                }

                // perform GA
                GeneticAlgorithm.sortSnakes(mySnakes);



                Snake[] children = new Snake[numChildren];
                for (int i = 0; i < numChildren; i++)
                {
                    Snake parent1;
                    Snake parent2;
                    Snake child;
                    // weighted parent selection based off of fitness
                    if (probabilisticParentSelection) {
                        parent1 = GeneticAlgorithm.selectRandomParent(mySnakes);
                        parent2 = GeneticAlgorithm.selectRandomParent(mySnakes);
                    }
                    else{
                        // only the n best snakes can be chosen as parents
                        parent1 = mySnakes[(int)(Math.random() * numParents)];
                        parent2 = mySnakes[(int)(Math.random() * numParents)];
                    }
                    child = GeneticAlgorithm.createChild(parent1,parent2);
                    children[i] = child;
                }

                // do mutations


                // populate next generation
                mySnakes = GeneticAlgorithm.createPopulation(populationSize);
                GeneticAlgorithm.integrateChildren(children, mySnakes);
            }
        }


            // *** headless training **
        // generation += 1
        // loop through snakes array

        // while snake.alive
            //snake.setInputs
            //snake.forwardProp
            //snake.chooseDirection (probabalistic, with softmax)
           // snake.move();
            //snake.checkApple();
            //snake.checkCollisions();
            // update best fitness

        // after loop is done
        // sort snakes
        // take n parents, take n children using k crossover
        // add new n best into the next generation
        // do random mutation





            // *** showing progression visually ***
            // loop
            // run generation
            // sort snakes
            // display best
            // repeat


        }
    }



