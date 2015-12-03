package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by Pro on 15/12/3.
 */
public class Kmeans {
    private ArrayList<double[]> centers = new ArrayList<double[]>();
    private int dim;

    public Kmeans(ArrayList<double[]> centers, int dim) {
        this.centers = centers;
        this.dim = dim;
    }

    public ArrayList<Integer> train(ArrayList<ArrayList<double[]>> data) {
        ArrayList<Integer> outs = new ArrayList<Integer>();
        ArrayList<double[]> old_center = this.centers;
        while (true) {
            // assign each points to each center
            outs.clear();
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).size(); j++) {
                    int cata = 0;
                    double dist = calculateDist(data.get(i).get(j), this.centers.get(0));
                    for (int k = 1; k < this.centers.size(); k++) {
                        if (calculateDist(data.get(i).get(j), this.centers.get(k)) < dist) {
                            cata = k;
                            dist = calculateDist(data.get(i).get(j), this.centers.get(k));
                        }
                    }
                    outs.add(cata);
                }
            }

            // recompute each center
            this.centers.clear();
            for (int i = 0; i < this.centers.size(); i++) {
                double[] newCenter = new double[dim];
                for (int c = 0; c < newCenter.length; c++) {
                    newCenter[c] = 0;
                }
                int offset = 0;
                int counter = 0;
                for (int j = 0; j < data.size(); j++) {
                    for (int k = 0; k < data.get(j).size(); k++) {
                        if (outs.get(offset) == i) {
                            for (int m = 0; m < data.get(j).get(k).length; i++) {
                                newCenter[m] += data.get(j).get(k)[m];
                                counter++;
                            }
                        }
                        offset++;
                    }
                }
                for (int n = 0; n < newCenter.length; n++) {
                    newCenter[n] /= counter;
                }
                this.centers.add(newCenter);
            }

            // stop when centers doesn't change
            if (old_center == this.centers)
                break;
            old_center = this.centers;
        }
        return outs;
    }


    public double calculateDist(double[] pattern, double[] center) {
        double dist = 0;
        for (int i = 0; i < pattern.length; i++) {
            dist += Math.pow(pattern[i] - center[i], 2);
        }
        return dist;
    }

}
