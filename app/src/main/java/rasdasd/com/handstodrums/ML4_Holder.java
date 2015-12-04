package rasdasd.com.handstodrums;

import java.util.ArrayList;
import java.util.Random;

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
    Random rand = new Random();
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
        int counter = 0;
        ArrayList<Integer> winners = new ArrayList<Integer>();
        for(int i = 0; i < votes.length; i++) {
            if (votes[i] == max) {
                counter++;
                winners.add(i);
            }
        }
        if(counter>1)
        {
            winner = winners.get(rand.nextInt(winners.size()));
        }
        return winner;
    }
}
