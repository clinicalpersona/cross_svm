/**
 * Created by dejanm on 11/11/16.
 */
public class TestKernels {
    public static double kernelDiff(double[][] k1, double[][] k2) {
        double diff = 0;

        for (int i = 0; i < k1.length; ++i) {
            for (int j = 0; j <= i; ++j) {
                diff += Math.abs(k1[i][j] - k2[i][j]);
            }
        }
        System.out.println("diff: " + diff);
        return diff;
    }

    public static void testLinear(double data[][], int[] indAll, int[] ind21, int[] ind22) {
        System.out.println("Linear Krnel");
        LinearKernel k1 = new LinearKernel(data);
        k1.addFeature(indAll, 1);
        k1.calculateKernel();
        k1.printKernel();

        LinearKernel k2 = new LinearKernel(data);
        k2.addFeature(ind21, 1);
        k2.addFeature(ind22, 1);
        k2.calculateKernel();
        k2.printKernel();

        kernelDiff(k1.getKernelCopy(), k2.getKernel());

        System.out.println();


        double[][] k2c = k2.getKernelCopy();
    }

    public static void testRBF(double data[][], int[] indAll, int[] ind21, int[] ind22) {
        System.out.println("RBF Krnel");
        double gamma = 0.05;

        RbfKernel k1 = new RbfKernel(data, gamma);
        k1.addFeature(indAll, 1);
        k1.calculateKernel();
        k1.printKernel();

        RbfKernel k2 = new RbfKernel(data, gamma);
        k2.addFeature(ind21, 1);
        k2.addFeature(ind22, 1);
        k2.calculateKernel();
        k2.printKernel();

        kernelDiff(k1.getKernelCopy(), k2.getKernel());

        System.out.println();
    }

    public static void testPoly(double data[][], int[] indAll, int[] ind21, int[] ind22) {
        System.out.println("Poly Krnel");
        double gamma = 0.05;
        double coef0 = .5;
        int deg = 2;

        PolyKernel k1 = new PolyKernel(data, gamma, coef0, deg);
        k1.addFeature(indAll, 1);
        k1.calculateKernel();
        k1.printKernel();

        PolyKernel k2 = new PolyKernel(data, gamma, coef0, deg);
        k2.addFeature(ind21, 1);
        k2.addFeature(ind22, 1);
        k2.calculateKernel();
        k2.printKernel();

        kernelDiff(k1.getKernelCopy(), k2.getKernel());

        System.out.println();
    }

    public static void testSigmoid(double data[][], int[] indAll, int[] ind21, int[] ind22) {
        System.out.println("Sigmoid Krnel");
        double gamma = 0.05;
        double coef0 = .5;

        SigmoidKernel k1 = new SigmoidKernel(data, gamma, coef0);
        k1.addFeature(indAll, 1);
        k1.calculateKernel();
        k1.printKernel();

        SigmoidKernel k2 = new SigmoidKernel(data, gamma, coef0);
        k2.addFeature(ind21, 1);
        k2.addFeature(ind22, 1);
        k2.calculateKernel();
        k2.printKernel();

        kernelDiff(k1.getKernelCopy(), k2.getKernel());

        System.out.println();
    }

    public static void main(String[] args) {
        double data[][] = {
                {0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0},
                {1, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0},
                {2, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0}
        };

        int[] indAll = {0, 1, 2, 3, 4, 5};
        int[] ind21 = {1, 2, 5};
        int[] ind22 = {0, 3, 4};

        testLinear(data, indAll, ind21, ind22);
        testRBF(data, indAll, ind21, ind22);
        testPoly(data, indAll, ind21, ind22);
        testSigmoid(data, indAll, ind21, ind22);
    }
}
