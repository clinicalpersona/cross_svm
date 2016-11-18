import java.util.HashSet;

/**
 * Created by dejanm on 11/11/16.
 */
public abstract class BaseKernel {
    protected double [][] reuseVal;
    protected double [][] kernel;
    protected final int numSamples;
    protected final int numFeatures;
    protected final double data[][];

    public BaseKernel(double[][] data) {
        this.data = data;
        this.numSamples = data.length;
        this.numFeatures = data[0].length;
    }

    public double[][] createMatrix() {
        double[][] mat = new double[numSamples][];
        int n = 1;
        for (int i = 0; i < numSamples; ++i) {
            mat[i] = new double[n];
            ++n;
        }
        return mat;
    }

    abstract void addFeature(int[] featureIndex, int fullModel);
    abstract void calculateKernel();

    public double dotProduct(double[] x, double[] y, int[] featureIndex) {
        double sum = 0;
        for (int i = 0; i < featureIndex.length; ++i) {
            int ind = featureIndex[i] + 1;
            sum += x[ind] * y[ind];
        }
        return sum;
    }

    public double dotProductSparse(double[] x, double[] y, HashSet<Integer> featureSet) {
        double sum = 0;

        int xlen = x.length;
        int ylen = y.length;
        int i = 2;
        int j = 2;
        while(i < xlen && j < ylen) {
            if(x[i] == y[j]) {
                if (featureSet.contains(x[i])) {
                    sum += x[i - 1] * y[j - 1];
                }
                i += 2;
                j += 2;
            }
            else if(x[i] > y[j]) {
                j += 2;
            }
            else {
                i += 2;
            }
        }
        return sum;
    }

    public double[][] getKernel() {
        return kernel;
    }

    public double[][] getKernelCopy() {
        double[][] kernelCopy = createMatrix();
        for (int i = 0; i < numSamples; ++i) {
            for (int j = i; j < numSamples; ++j) {
                kernelCopy[j][i] = kernel[j][i];
            }
        }
        return kernelCopy;
    }

    public void printKernel() {
        for (int i = 0; i < numSamples; ++i) {
            for (int j = 0; j <= i; ++j) {
                System.out.print(kernel[i][j] + " ");
            }
            System.out.println();
        }
    }
}
