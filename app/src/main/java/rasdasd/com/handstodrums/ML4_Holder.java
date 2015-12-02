package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by David on 10/21/2015.
 */
public class ML4_Holder implements Holder {
    Holder[] holders;
    int classes;

    public ML4_Holder(Holder[] holders, int classes) {
        this.holders = holders;
        this.classes = classes;
    }

    public void addDataPoint(float[] datapoint, int cata) {

    }

    public void train() {

    }

    public int classify(float[] datapoint) {
        int[] votes = new int[classes];
        for (Holder h : holders) {
            votes[h.classify(datapoint)]++;
        }
        int max = -1;
        int winner = -1;
        for (int i = 0; i < votes.length; i++) {
            if (votes[i] > max) {
                max = votes[i];
                winner = i;
            }
        }
        return winner;
    }
}
