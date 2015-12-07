package rasdasd.com.handstodrums;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by David on 10/21/2015.
 */
//Updated Jeremy Li 28 on 12/6/2015
public class ML3_Holder implements Holder{
    ArrayList<ArrayList<double[]>> data = new ArrayList<ArrayList<double[]>>();
    private ArrayList<double[]> centers = new ArrayList<double[]>();
    private int inputsize;
    public int classes;
    private static final String TAG = "MyActivity";

    public ML3_Holder(int classes, int inputsize)
    {
        this.classes = classes;
        this.inputsize = inputsize;
        for(int i = 0; i < classes; i++)
            data.add(new ArrayList<double[]>());
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

        this.centers.clear();
        for(int i=0; i<data.size();i++){
            double[] newCenter = new double[inputsize];
            for(int c=0; c<newCenter.length;c++)
                newCenter[c] = 0;
            int counter = 0;
            for(int k=0; k<data.get(i).size();k++){
                for(int m=0; m<data.get(i).get(k).length; m++){
                    newCenter[m] += data.get(i).get(k)[m];
                    counter ++;
                }
            }
            for(int n=0; n<newCenter.length;n++){
                newCenter[n] /= counter;
            }
            this.centers.add(newCenter);
        }
//        Log.d(TAG, "train: "+this.centers.size());
    }
    public int classify(float[] datapoint)
    {
        int cata = 0;
        double[] input = new double[datapoint.length];
        for (int i = 0; i < datapoint.length; i++)
        {
            input[i] = datapoint[i];
        }

        double max = calculateDist(input, this.centers.get(0));
        for(int i=0; i< this.centers.size(); i++){
            double dist = calculateDist(input,this.centers.get(i));
            if(dist < max){
                cata = i;
                max = dist;
            }
        }
//        Log.d(TAG, "classify: "+cata);
        return cata;
    }

    public double calculateDist(double[] pattern, double[] center){
        double dist=0;
        for(int i=0;i<pattern.length;i++){
            dist += Math.pow(pattern[i]-center[i],2);
        }
        return dist;
    }
}