import java.util.*;
public class GeneticAlgorithm {

    /**
     * Creates a population of Snake objects with the specified size.
     *
     * @param size The size of the population.
     * @return An array of Snake objects representing the population.
     */
    public static Snake[] createPopulation(int size)
    {
        Snake[] snakeArray = new Snake[size];
        for (int i = 0; i < snakeArray.length; i++){
            snakeArray[i] = new Snake();
        }
        return snakeArray;
    }

    /**
     * Sorts an array of Snake objects by fitness in descending order.
     *
     * @param array The array of Snake objects to be sorted.
     */
    public static void sortSnakes(Snake[] array){
        Arrays.sort(array, (Snake a, Snake b) -> b.fitness - a.fitness);
    }

    /**
     * Selects a random parent Snake from an array based on fitness.
     *
     * @param array The array of Snake objects to choose a parent from.
     * @return A randomly selected Snake object.
     */
    public static Snake selectRandomParent(Snake[] array){
        int fitnessSum = 0;
        for (Snake value : array) {
            fitnessSum += value.fitness;
        }

        int position =(int) (Math.random() * fitnessSum);
        int sum = 0;
        for (Snake snake : array) {
            sum += snake.fitness;
            if (sum >= position) {
                return snake;
            }
        }
        return null;
    }


    /**
     * Creates a child Snake by combining genes from two parent Snakes.
     *
     * @param parent1 The first parent Snake.
     * @param parent2 The second parent Snake.
     * @return A new Snake representing the child.
     */
    public static Snake createChild(Snake parent1, Snake parent2){
        Snake child = new Snake();
        child.brain.architecture = parent1.brain.architecture;

        double[] parent1Genes = NeuralNetwork.tensorToVector(parent1.brain.weights);
        double[] parent2Genes = NeuralNetwork.tensorToVector(parent2.brain.weights);

        double[] childGenes;

        if (SnakeGame.crossoverType == 0){
            childGenes = uniformCrossover(parent1Genes,parent2Genes,SnakeGame.crossoverRate);
        }
        else{
            childGenes = kPointCrossover(parent1Genes,parent2Genes,SnakeGame.crossoverType);
        }

        // importing the resulting genes back into the child
        child.brain.weights = NeuralNetwork.vectorToTensor(childGenes,child.brain.architecture);
        return child;

    }

    /**
     * Integrates an array of children Snakes into a population.
     *
     * @param children   The array of child Snakes.
     * @param population The array representing the population to integrate the children into.
     */
    public static void integrateChildren(Snake[] children, Snake[] population){
        System.arraycopy(children, 0, population, 0, children.length);
    }

    /**
     * Performs uniform crossover on the genes of two parent Snakes.
     *
     * @param parent1Genes   The genes of the first parent Snake.
     * @param parent2Genes   The genes of the second parent Snake.
     * @param crossoverRate  The percentage of genes each parent contributes to the child.
     * @return An array representing the genes of the resulting child Snake.
     */
    public static double[] uniformCrossover(double[] parent1Genes, double[] parent2Genes, double crossoverRate){
        double[] childGenes = new double[parent1Genes.length];


        for (int i = 0; i < childGenes.length; i++){
            double r = Math.random();
            if (r > crossoverRate){
                childGenes[i] = parent1Genes[i];
            }
            else{
                childGenes[i] = parent2Genes[i];
            }
        }
        return childGenes;
    }


    /**
     * Performs k-point crossover on the genes of two parent Snakes.
     *
     * @param parent1Genes The genes of the first parent Snake.
     * @param parent2Genes The genes of the second parent Snake.
     * @param k            The number of crossover points.
     * @return An array representing the genes of the resulting child Snake.
     */
    public static double[] kPointCrossover(double[] parent1Genes, double[] parent2Genes, int k){
        int[] points = new int[k + 2];
        points[0] = 0;
        points[k+1] = parent1Genes.length;
        for (int i = 1; i < points.length - 1; i++){
            points[i] =(int) (Math.random() * parent1Genes.length);
        }
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++){
            // swap elements in a and b from index points[i] to points[i+1], but it only happens every other turn
            if (i % 2 == 0){
                for (int j = points[i]; j < points[i+1]; j++){
                    // swap elements a[j] and b[j]
                    double temp = parent1Genes[j];
                    parent1Genes[j] = parent2Genes[j];
                    parent2Genes[j] = temp;
                }
            }
        }
        return parent1Genes;
    }
}
