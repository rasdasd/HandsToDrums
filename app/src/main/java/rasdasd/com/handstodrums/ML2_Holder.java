package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by robert on 12/2/2015.
 */
public class ML2_Holder implements Holder{
    //Initialize training set ArrayList:
    ArrayList<float[]> x_train = new ArrayList<>();
    ArrayList<Integer> y_train = new ArrayList<>();
    //Initialize learning parameters:
    MulticlassSVM MCsvm;
    public int num_classes = 0;
    //Define ML2_Holder class constructor:
    public ML2_Holder(int classes, int inputsize)
    {
        /**
         * Inputs:
         * classes = number of classes in training data
         * inputsize = number of features/floats per datapoint
         */
        //Assign class parameters:
        num_classes = classes;
        MCsvm = new MulticlassSVM();
    }
    public void addDataPoint(float[] datapoint, int cata)
    {
        /**
         * Inputs:
         * datapoint = training data of format ['xarrfft' float[fftsize*2], 'yarrfft' float[fftsize*2], 'zarrfft' float[fftsize*2], 'xarr' float[fftsize], 'yarr' float[fftsize], 'zarr' float[fftsize]]
         * cata = target category or class of datapoint
         */
        //Append new datapoint to x_train and new target to y_train:
        x_train.add(datapoint.clone());
        y_train.add(cata);
    }
    public void train()
    {
        MCsvm.train(x_train,y_train);
    }
    public int classify(float[] datapoint)
    {
        /**
         * Inputs:
         * datapoint = testing data of format ['xarrfft' float[fftsize*2], 'yarrfft' float[fftsize*2], 'zarrfft' float[fftsize*2], 'xarr' float[fftsize], 'yarr' float[fftsize], 'zarr' float[fftsize]]
         */

        int predicted_class = MCsvm.classify(datapoint);
        return predicted_class;
    }
}
