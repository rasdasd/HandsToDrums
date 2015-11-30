package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by David on 11/30/2015.
 */
public interface Holder {

    public void addDataPoint(float[] datapoint, int cata);

    public void train();

    public int classify(float[] datapoint);
}
