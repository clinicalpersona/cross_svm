import java.util.HashSet;

/**
 * Created by dejanm on 11/11/16.
 */
public class PolyKernel extends BaseKernel {
    private double gamma;
    private double coef0;
    private int degree;
    public PolyKernel(double[][] data, double gamma, double coef0, int degree) {
        super(data);
        this.gamma = gamma;
        this.coef0 = coef0;
        this.degree = degree;
        reuseVal = createMatrix();
        kernel = createMatrix();
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setCoef0(double coef0) {
        this.coef0 = coef0;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void addFeature(int[] featureIndex, int fullModel) {
        if (fullModel == 1) {
            for (int i = 0; i < numSamples; ++i) {
                for (int j = i; j < numSamples; ++j) {
                    reuseVal[j][i] += dotProduct(data[i], data[j], featureIndex);
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
                    reuseVal[j][i] += dotProductSparse(data[i], data[j], featureSet);
                }
            }
        }

    }

    public void calculateKernel() {
        for (int i = 0; i < numSamples; ++i) {
            for (int j = i; j < numSamples; ++j) {
                kernel[j][i] = powi(gamma * reuseVal[j][i] + coef0, degree);
            }
        }
    }

    private double powi(double base, int times) {
        double tmp = base;
        double ret = 1.0;
        for(int t = times; t > 0; t /= 2) {
            if(t%2 == 1) ret *= tmp;
            tmp = tmp * tmp;
        }
        return ret;
    }
}
