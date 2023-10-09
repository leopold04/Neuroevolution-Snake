import java.util.*;
public class GeneticAlgorithm {

    public static Snake[] createPopulation(int size)
    {
        Snake[] snakeArray = new Snake[size];
        for (int i = 0; i < snakeArray.length; i++){
            snakeArray[i] = new Snake();
        }
        return snakeArray;
    }

    // sorts snakes by fitness (in descending order)
    public static void sortSnakes(Snake[] array){
        Arrays.sort(array, (Snake a, Snake b) -> b.fitness - a.fitness);
    }

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

    public static void integrateChildren(Snake[] children, Snake[] population){
        System.arraycopy(children, 0, population, 0, children.length);
    }

    // crossover rate is roughly the percentage of genes that each parent will have in the resultant child
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

    public static double[] kPointCrossover(double[] parent1Genes, double[] parent2Genes, int k){
        // k point means 5 different partitions
        // example 3 point
        // 0-p1 p1-p2 p2-p3 p3-n
        // we need array of 3 points and array length
        // also method to swap elements in section of array
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
