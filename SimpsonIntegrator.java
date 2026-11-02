public class SimpsonIntegrator {

    public SimpsonIntegrator() {
    }

    public double evaluate(int bound1, int bound2, Polynomial function) {
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null.");
        }
        if (bound2 <= bound1) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound.");
        }

        int n = 1000;
        double deltaX = (bound2 - bound1) / (double) n;
        double sum = function.evaluate(bound1) + function.evaluate(bound2);

        for (int i = 1; i < n; i++) {
            double x = bound1 + i * deltaX;
            if (i % 2 == 0) {
                sum += 2.0 * function.evaluate(x);
            } else {
                sum += 4.0 * function.evaluate(x);
            }
        }

        return (deltaX / 3.0) * sum;
    }
}
