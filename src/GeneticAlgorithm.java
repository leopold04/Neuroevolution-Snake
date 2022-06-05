import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GeneticAlgorithm
{
    public int popSize;;
    public int totalSnakes = 0;
    // Uniform or Two-Point
    public String crossoverType = "Two Point";
    public Snake[] generatePopulation(int populationSize)
    {
        popSize = populationSize;
        Snake[] mySnakes = new Snake[populationSize];
        for (int i = 0; i < populationSize; i++)
        {
            mySnakes[i] = new Snake();
            mySnakes[i].iD = totalSnakes;
            totalSnakes++;
            mySnakes[i].setPosition(250,270);
         //  mySnakes[i].setRandomPosition();
        }
        return mySnakes;
    }

    // sorts in descending order
    public Snake[] sortSnakes(Snake[] mySnakes)
    {
        /* bubble sort */
        for (int i = 0; i < mySnakes.length; i++)
        {
            for (int j = 0; j < mySnakes.length - i - 1; j++)
            {
                if (mySnakes[j].fitness < mySnakes[j+1].fitness)
                {
                    Snake temp = mySnakes[j];
                    mySnakes[j] = mySnakes[j+1];
                    mySnakes[j+1] = temp;
                }
            }
        }
        return mySnakes;
    }

    // probability to be selected based off of fitness
    public double[] setProbabilities(Snake[] mySnakes)
    {
        double[] probabilities = new double[popSize];
        double totalFitness = 0;
        for (Snake snake : mySnakes)
        {
            totalFitness += snake.fitness;
        }

        for (int i = 0; i < probabilities.length; i++)
        {
            probabilities[i] =  mySnakes[i].fitness / totalFitness;
        }
        return probabilities;
    }

    // array of parents
    public Snake[] wheelSelection(Snake[] mySnakes,int numParents)
    {
        double[] parentProbabilities = setProbabilities(mySnakes);
        Snake[] parents = new Snake[numParents];

        for (int p = 0; p < numParents; p++)
        {

            // roulette wheel
/*
            double num = Math.random();
            int idx = 0;
            while (num > 0)
            {
                num -= parentProbabilities[idx];
                idx++;
            }
            parents[p] = mySnakes[idx-1];

*/
            // best numparents parents
            parents[p] = sortSnakes(mySnakes)[p];
        }
        return parents;
    }

    public Snake[] reproduceChildren(Snake[] parents, int numChildren)
    {
        Snake[] children = new Snake[numChildren];
        for (int i = 0; i < numChildren; i++)
        {
            Snake parent1 = parents[(int)(Math.random() * parents.length)];
            Snake parent2 = parents[(int)(Math.random() * parents.length)];
            Snake[] parentSelectionArr = new Snake[]{parent1,parent2};
            Snake child = new Snake();
            if (crossoverType.equals("Uniform"))
            {
                double crossRatio = Math.random();
                for (int k = 0; k < child.chromosome.length; k++)
                {
                    double crossProb = Math.random();
                    if (crossProb >= crossRatio)
                    {
                        child.chromosome[k] = parent1.chromosome[k];
                    }
                    else
                    {
                        child.chromosome[k] = parent2.chromosome[k];
                    }
                }
            }
            if (crossoverType.equals("Two Point"))
            {
                int[] crossPoints = new int[]{(int)(Math.random() * child.chromosome.length),(int)(Math.random() * child.chromosome.length)};
                int crossPoint1 = Math.min(crossPoints[0],crossPoints[1]);
                int crossPoint2 = Math.max(crossPoints[0],crossPoints[1]);

                for (int j = 0; j < crossPoint1 + 1; j++)
                {
                    child.chromosome[j] = parent1.chromosome[j];
                }

                for (int k = crossPoint1 + 1; k < crossPoint2 + 1; k++)
                {
                    child.chromosome[k] = parent2.chromosome[k];

                }

                for (int l = crossPoint2 + 1; l < child.chromosome.length; l++)
                {

                    child.chromosome[l] = parent2.chromosome[l];

                }
            }

            children[i] = child;
        }
        return children;
    }

    public void mutateChildren(Snake[] mySnakes, int mutateCount)
    {
       for (Snake snake : mySnakes){
           for (int i = 0; i < mutateCount; i++)
           {
               int randIdx = (int)(Math.random() * snake.chromosome.length);
               //snake.chromosome[randIdx] = Math.random();
               snake.chromosome[randIdx] = Math.random() * 4 - 2;
               if (Math.random() < 0.01){
                   snake.chromosome[randIdx] = 0;
               }
           }
       }
    }

    // integrates children and puts k best snakes in next generation
    public void integrateChildren(Snake[] mySnakes, Snake[] children, int bestSnakesNum)
    {
        Snake[] sortedSnakes = sortSnakes(mySnakes);

        for (int i = 0; i < bestSnakesNum; i++)
        {
            mySnakes[i] = sortedSnakes[i];
            mySnakes[i].iD = sortedSnakes[i].iD;
        }
        //     if (bestSnakesNum >= 0) System.arraycopy(sortedSnakes, 0, mySnakes, 0, bestSnakesNum);
        for (int k = bestSnakesNum; k < children.length; k++)
        {
            mySnakes[k] = children[k];
        }
       // if (children.length - bestSnakesNum >= 0) System.arraycopy(children, bestSnakesNum, mySnakes, bestSnakesNum, children.length - bestSnakesNum);
    }

    public void resetFitnessValues(Snake[] mySnakes)
    {
        for (int i = 0; i < mySnakes.length; i++){
            mySnakes[i].fitness = 0;
        }
    }

    public Snake[] createNextGeneration(Snake[] mySnakes, int numParents, int numChildren, int bestSnakesNum, int mutateCount)
    {
        sortSnakes(mySnakes);
        Snake[] parents = wheelSelection(mySnakes,numParents);
        Snake[] children = reproduceChildren(parents,numChildren);
        integrateChildren(mySnakes,children,bestSnakesNum);
        mutateChildren(mySnakes, mutateCount);
        Snake[] newPopulation = generatePopulation(popSize);

        // transfering genes of old snakes to brand new population
        for (int i = 0; i < newPopulation.length; i++)
        {
            newPopulation[i].chromosome = Arrays.copyOf(mySnakes[i].chromosome,mySnakes[i].chromosome.length);
            newPopulation[i].assignGenes();
        }
        return newPopulation;
    }

    public double averageApples(Snake[] mySnakes)
    {
        double sum = 0;
        for (int i = 0; i < mySnakes.length; i++)
        {
            sum += mySnakes[i].applesEaten;
        }
        return (sum / mySnakes.length);
    }

    public void printBestSnakes(Snake[] mySnakes, int numSnakes, int generationNum)
    {
        System.out.println();
        System.out.println("Best Snakes in Generation: " + generationNum);
        for (int i = 0; i < numSnakes; i++)
        {
            System.out.println("Apples Eaten: " + sortSnakes(mySnakes)[i].applesEaten + " Fitness: " + sortSnakes(mySnakes)[i].fitness +  "moves:  " + sortSnakes(mySnakes)[i].totalMoves + " Chromosome: " + sortSnakes(mySnakes)[i].exportChromosome());
        }
    }
}
