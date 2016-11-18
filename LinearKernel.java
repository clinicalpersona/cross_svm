import java.util.HashSet;

/**
 * Created by dejanm on 11/11/16.
 */
public class LinearKernel extends BaseKernel {
    public LinearKernel(double[][] data) {
        super(data);
        reuseVal = createMatrix();
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
        kernel = reuseVal;
    }
}
