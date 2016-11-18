import java.util.HashSet;

/**
 * Created by dejanm on 11/11/16.
 */
public class SigmoidKernel extends BaseKernel {
    private double gamma;
    private double coef0;

    public SigmoidKernel(double[][] data, double gamma, double coef0) {
        super(data);
        this.gamma = gamma;
        this.coef0 = coef0;
        reuseVal = createMatrix();
        kernel = createMatrix();
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setCoef0(double coef0) {
        this.coef0 = coef0;
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
                kernel[j][i] = Math.tanh(gamma * reuseVal[j][i] + coef0);
            }
        }
    }
}



