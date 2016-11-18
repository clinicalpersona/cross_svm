import java.util.HashSet;

/**
 * Created by dejanm on 11/11/16.
 */
public class RbfKernel extends BaseKernel {
    private double gamma;

    public RbfKernel(double[][] data, double gamma) {
        super(data);
        this.gamma = gamma;
        reuseVal = createMatrix();
        kernel = createMatrix();
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void addFeature(int[] featureIndex, int fullModel) {
        if (fullModel == 1) {
            for (int i = 0; i < numSamples; ++i) {
                for (int j = i; j < numSamples; ++j) {
                    reuseVal[j][i] += euclidianDistance(data[i], data[j], featureIndex);
                }
            }
        }
        else {
            HashSet<Integer> featureSet = new HashSet<Integer>();
            for (int f: featureIndex ) {
                featureSet.add(f);
            }
            for (int i = 0; i < numSamples; ++i) {
                for (int j = i; j < numSamples; ++j) {
                    reuseVal[j][i] += euclidianDistanceSparse(data[i], data[j], featureSet);
                }
            }
        }
    }

    public void calculateKernel() {
        for (int i = 0; i < numSamples; ++i) {
            for (int j = i; j < numSamples; ++j) {
                kernel[j][i] = Math.exp(-gamma * reuseVal[j][i]);
            }
        }
    }

    private double euclidianDistance(double[] x, double[] y, int[] featureIndex) {
        double dist = 0;
        for (int i = 0; i < featureIndex.length; ++i) {
            int ind = featureIndex[i] + 1;
            double d = x[ind] - y[ind];
            dist += d * d;
        }

        return dist;
    }

    private double euclidianDistanceSparse(double[] x, double[] y, HashSet<Integer> featureSet) {
        double dist = 0;
        int xlen = x.length;
        int ylen = y.length;
        int i = 2;
        int j = 2;
        while(i < xlen && j < ylen)
        {
            if(x[i] == y[j])
            {
                if (featureSet.contains(x[i])) {
                    double d = x[i - 1] - y[j - 1];
                    dist += d * d;
                }
                i += 2;
                j += 2;
            }
            else if(x[i] > y[j])
            {
                if (featureSet.contains(y[i])) {
                    dist += y[j - 1] * y[j - 1];
                }
                j += 2;
            }
            else
            {
                if (featureSet.contains(x[i])) {
                    dist += x[i - 1] * x[i - 1];
                }
                i += 2;
            }
        }
        while(i < xlen)
        {
            if (featureSet.contains(x[i])) {
                dist += x[i - 1] * x[i - 1];
            }
            i += 2;
        }
        while(j < ylen)
        {
            if (featureSet.contains(y[j])) {
                dist += y[j - 1] * y[j - 1];
            }
            j += 2;
        }

        return dist;
    }
}
