public class NeuralNetwork
{
    int numInputs = 16;
    // ratio of hidden nodes to inputs
    double hiddenRatio = 0.625;
    int numHiddenLayers = 1;
    public int numOutputs = 4;
    double[] inputs;
    double[] hiddenNodes;
    double[][] weightsHidden;
    double[][] weightsInput;
    double[] outputs;

    public NeuralNetwork()
    {
        inputs = new double[numInputs];
        hiddenNodes = new double[(int) (inputs.length * hiddenRatio)];
        weightsInput = new double[inputs.length][hiddenNodes.length];
        weightsHidden = new double[hiddenNodes.length][numOutputs];
        outputs = new double[numOutputs];
        setRandomWeights(weightsInput);
        setRandomWeights(weightsHidden);
    }

    public void setRandomWeights(double[][] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            for (int k = 0; k < array[i].length; k++)
            {
                // sets weights to double between -2 and 2
                 array[i][k] = Math.random() * 4 - 2;
            }
        }
    }

    public void forwardPropagation()
    {
        for (int i = 0; i < hiddenNodes.length; i++)
        {
            hiddenNodes[i] = 0;
            for (int k = 0; k < inputs.length; k++)
            {
                hiddenNodes[i] += inputs[k] * weightsInput[k][i];
            }
            hiddenNodes[i] = sigmoid(hiddenNodes[i]);
        }

        for (int j = 0; j < numOutputs; j++)
        {
            for(int m = 0; m < hiddenNodes.length; m++)
            {
                outputs[j] += hiddenNodes[m] * weightsHidden[m][j];
            }
            outputs[j] = sigmoid(outputs[j]);
        }

    }


    public static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(x * -1));
    }

    public double relu(double x)
    {
        if (x > 0)
        {
            return x;
        }
        return 0;
    }
}
