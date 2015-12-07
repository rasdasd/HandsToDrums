package rasdasd.com.handstodrums;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David on 10/21/2015.
 */
public class ML1_Holder implements Holder {
    ArrayList<ArrayList<double[]>> data = new ArrayList<ArrayList<double[]>>();
    ArrayList<double[]> desiredouts = new ArrayList<double[]>();
    public int classes = 0;
    DeepMLP mlp;
    int outputsize;
    int iterations = 100;
    int layerCount = 5;
    double[][] predictionBias;

    public ML1_Holder(int classes, int inputsize) {
        outputsize = Integer.SIZE - Integer.numberOfLeadingZeros(classes - 1);
        System.out.println(outputsize);
        this.classes = classes;
        for (int i = 0; i < classes; i++) {
            data.add(new ArrayList<double[]>());
            double[] desire = new double[outputsize];
            for (int j = outputsize - 1; j >= 0; j--) {
                boolean flag = (i & (1 << j)) != 0;
                if (flag)
                    desire[j] = 1;
                else
                    desire[j] = 0;
            }
            desiredouts.add(desire);
        }
        int[] layers = new int[layerCount];
        layers[0] = inputsize;
        int descent = (inputsize - outputsize) / layerCount;
        for (int i = 1; i < layerCount - 1; i++) {
            layers[i] = layers[i - 1] - descent;
        }
        layers[layerCount - 1] = outputsize;
        predictionBias = new double[outputsize][3];
        for(int i = 0; i < outputsize; i++)
        {
            predictionBias[i][0] = Double.MIN_VALUE;
            predictionBias[i][1] = Double.MAX_VALUE;
        }

        mlp = new DeepMLP(layers);
    }

    public void addDataPoint(float[] datapoint, int cata) {
        double[] output = new double[datapoint.length];
        for (int i = 0; i < datapoint.length; i++) {
            output[i] = datapoint[i];
        }
        data.get(cata).add(output);
    }
    public void train() {
        mlp.setEpochs(iterations);
        for (int c = 0; c < classes; c++) {
            for (int v = 0; v < data.get(c).size(); v++) {
                double[] pattern = data.get(c).get(v);
                mlp.StochasticLearning(pattern, desiredouts.get(c));
            }
        }
        for (int c = 0; c < classes; c++) {
            for (int v = 0; v < data.get(c).size(); v++) {
                double[] pattern = data.get(c).get(v);
                double[] output = mlp.Predict(pattern);
                for(int i = 0 ; i < output.length; i++) {
                    double val = output[i];
                    if(val > predictionBias[i][0])
                        predictionBias[i][0] = val;
                    if(val < predictionBias[i][1])
                        predictionBias[i][1] = val;
                }
            }
        }
        for(int i = 0; i < outputsize; i++)
        {
            predictionBias[i][2] = (predictionBias[i][0]+predictionBias[i][1])/2.0;
        }
    }

    public int classify(float[] datapoint) {
        int cata = -1;
        double[] input = new double[datapoint.length];
        for (int i = 0; i < datapoint.length; i++) {
            input[i] = datapoint[i];
        }
        double[] output = mlp.Predict(input);
        System.out.println(Arrays.toString(output));
        int result = 0;
        for (int i = output.length - 1; i >= 0; i--)
            if (output[i] >= predictionBias[i][2])
                result += Math.pow(2, (output.length - i - 1));
        cata = result;
        return cata;
    }
}
