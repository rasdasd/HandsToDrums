package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by David on 10/21/2015.
 */
public class ML2_Holder implements Holder{
    ArrayList<ArrayList<double[]>> data = new ArrayList<ArrayList<double[]>>();
    ArrayList<double[]> desiredouts = new ArrayList<double[]>();
    public int classes = 0;
    MLP mlp;
    int outputsize;
    int iterations = 10000;
    public ML2_Holder(int classes, int inputsize)
    {
        outputsize = Integer.SIZE-Integer.numberOfLeadingZeros(classes-1);
        this.classes = classes;
        for(int i = 0; i < classes; i++)
        {
            data.add(new ArrayList<double[]>());
            double[] desire = new double[outputsize];
            for (int j = outputsize-1; j >= 0; j--) {
                boolean flag = (i & (1 << j)) != 0;
                if(flag)
                    desire[j] = 1;
                else
                    desire[j] = 0;
            }
            desiredouts.add(desire);
        }
        int hidden = (inputsize+outputsize)*2/3;
        mlp = new MLP(inputsize, hidden, outputsize);
    }
    public void addDataPoint(float[] datapoint, int cata)
    {
        double[] output = new double[datapoint.length];
        for (int i = 0; i < datapoint.length; i++)
        {
            output[i] = datapoint[i];
        }
        data.get(cata).add(output);
    }
    public void train()
    {
        for(int i = 0; i < iterations; i++)
        {
            for(int c = 0; c < classes; c++)
            {
                for(int v = 0; v < data.get(c).size(); v++)
                {
                    double[] pattern = data.get(c).get(v);
                    mlp.train(pattern,desiredouts.get(c));
                }
            }
        }
    }
    public int classify(float[] datapoint)
    {
        int cata = -1;
        double[] input = new double[datapoint.length];
        for (int i = 0; i < datapoint.length; i++)
        {
            input[i] = datapoint[i];
        }
        double[] output = mlp.passNet(input);
        int result = 0;
        for(int i=output.length - 1; i>=0; i--)
        if(output[i]>=0.5)
            result += Math.pow(2, (output.length-i - 1));
        cata = result;
        return cata;
    }
}
