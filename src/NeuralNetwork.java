import java.util.Arrays;

public class NeuralNetwork {


    double[][][] weights;
    double[][] nodes;

    int[] architecture;


    enum ActivationFunction{
        SIGMOID, TANH, RELU, SOFTMAX
    }

    ActivationFunction layerActivation = ActivationFunction.RELU;

    ActivationFunction outputActivation = ActivationFunction.SIGMOID;


    public NeuralNetwork(int[] arch)
    {
        architecture = arch;
        // creating matrix (2D array) of nodes
        nodes = new double[architecture.length][];
        // creating the nodes in each layer
        for (int i = 0; i < nodes.length; i++){
            // for example, if architecture = [14,8,4], then our first layer of nodes has 14, our second has 8, and our third has 4
            nodes[i] = new double[architecture[i]];
        }

        // creating tensor (list of 2D array) of weights
        // the weights between each layer can be seen as a 2D array
       // if our architecture is [14,8,4], then our weights array will be an array containing 2D arrays
        // of dimensions [14x8],[8x4]
        weights = new double[architecture.length - 1][][];
        for (int i = 0; i < architecture.length - 1; i++){
            // for 1 layer, each node corresponds to a row, for example, a NN that is [1,2] has a weight array that has 1 row, and 2 columns
            // so weights[0] = new double[1][2];
            // w[i][j][k] = w[i][src][dest]
            weights[i] = new double[architecture[i]][architecture[i+1]];
        }

        // we also initialize with random weights
        setRandomWeights();
    }

    public void setRandomWeights(){
        int minWeight = SnakeGame.weightRange[0];
        int maxWeight = SnakeGame.weightRange[1];
        for (int i = 0; i < weights.length; i++){
            for (int j = 0; j < weights[i].length; j++){
                for (int k = 0; k < weights[i][j].length; k++){
                    // numbers from -1 to 1
                    weights[i][j][k] = (maxWeight - minWeight) * Math.random() + minWeight;
                }
            }
        }
    }

    public void forwardPropagation(){
        // if we have 3 layers, we propagate twice, since there are 2 sets of weights
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < nodes[i+1].length; j++){
                double value = 0;
                for (int k = 0; k < nodes[i].length; k++){
                    // since w[i][a][b] = w[i][src][dest], for a fixed dest in the next layer, we loop through all the other nodes attached to it
                    // this is correct
                    value += nodes[i][k] * weights[i][k][j];
                }
                 nodes[i+1][j] = relu(value);
                // output layer
                if (i == weights.length - 1){
                     nodes[i+1][j] = sigmoid(value);
                }


            }
        }
    }



    public static double sigmoid(double num){
           return 1 / (1 + Math.exp(-1 * num));
    }

    public static double tanh(double num){
       return Math.tanh(num);
    }


    public double[] softmax(double[] array)
    {
        double sum = 0;
        double s = 0;
        for (int i = 0; i < array.length; i++)
        {
            sum += Math.exp(array[i]);
        }
        for (int j = 0; j < array.length; j++) {
            array[j] = Math.exp(array[j]) / sum;
            s += array[j];
        }
        System.out.println((s));
        return array;

    }

    public double relu(double num){
        return Math.max(num,0);
    }

    public static double[] tensorToVector(double[][][] tensor){
        // flattens the tensor into a vector (3D array to 1D array)
        int elementsInTensor = 0;
        for (double[][] matrix : tensor){
            for (double[] vector : matrix){
                for (double element : vector){
                    elementsInTensor++;
                }
            }
        }
        double[] newVector = new double[elementsInTensor];
        int pointer = 0;
        for (double[][] matrix : tensor){
            for (double[] vector : matrix){
                for (double element : vector){
                    newVector[pointer] = element;
                    pointer++;
                }
            }
        }
        return newVector;
    }

    public String exportWeightVector(){
        // exports architecture and weight vector
        String arc = Arrays.toString(architecture);
        String wei = Arrays.toString(tensorToVector(weights));
        return arc + wei;
    }


    public void importWeightVector(String vector){
        String archString = vector.substring(vector.indexOf('[') + 1, vector.indexOf(']'));
        String[] archStringArray = archString.split(", ");
        architecture = Arrays.stream(archStringArray).mapToInt(Integer::parseInt).toArray();


        String weightString = vector.substring(vector.lastIndexOf('[') + 1, vector.lastIndexOf(']'));
        String[] weightStringArray = weightString.split(", ");
        double[] weightVector = Arrays.stream(weightStringArray).mapToDouble(Double::parseDouble).toArray();
        weights = vectorToTensor(weightVector,architecture);
    }

    // given a 1d list of weights, and the architecture, turn a vector into a matrix
    public static double[][][] vectorToTensor(double[] vector, int[] architecture){
        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
        // [1,2,3,4]
        // [axb] = [number of mini arrays x length of those mini arrays] (or rows x columns)
        // so we have t[0] = [1x2], t[1] = [2x3], t[2] = [3x4] for a total of 20 weights
        //    t[0] = [1,2]
        //    t[1] = [3,4,5][6,7,8]
        //    t[2] = [9,10,11,12][13,14,15,16][17,18,19,20]
        double[][][] tensor = new double[architecture.length - 1][][];
        // index we are taking from
        int pointer = 0;

        for (int i = 0; i < architecture.length - 1; i++){
            tensor[i] = new double[architecture[i]][architecture[i+1]];
            for (int j = 0; j < tensor[i].length; j++){
                for (int k = 0; k < tensor[i][j].length; k++){
                    tensor[i][j][k] = vector[pointer];
                    pointer+= 1;
                }
            }

        }
        return tensor;
    }

    }

