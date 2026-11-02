import java.util.ArrayList;
import java.util.List;

public class Polynomial {

    public double[] coefficients;
    public int[] powers;

    public Polynomial(double[] coefficients, int[] powers) {
        if (coefficients == null || powers == null) {
            throw new IllegalArgumentException("Coefficients and powers cannot be null.");
        }
        if (coefficients.length != powers.length) {
            throw new IllegalArgumentException("Coefficients and powers must be the same length.");
        }
        this.coefficients = coefficients.clone();
        this.powers = powers.clone();
    }

    public double evaluate(double x) {
        double result = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, powers[i]);
        }
        return result;
    }

    public Polynomial differentiate() {
        List<Double> derivCoefficients = new ArrayList<>();
        List<Integer> derivPowers = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            if (powers[i] == 0) {
                continue;
            }
            derivCoefficients.add(coefficients[i] * powers[i]);
            derivPowers.add(powers[i] - 1);
        }

        if (derivCoefficients.isEmpty()) {
            return new Polynomial(new double[]{0.0}, new int[]{0});
        }

        double[] newCoefficients = new double[derivCoefficients.size()];
        int[] newPowers = new int[derivPowers.size()];

        for (int i = 0; i < derivCoefficients.size(); i++) {
            newCoefficients[i] = derivCoefficients.get(i);
            newPowers[i] = derivPowers.get(i);
        }

        return new Polynomial(newCoefficients, newPowers);
    }

    public Polynomial integrate() {
        List<Double> integralCoefficients = new ArrayList<>();
        List<Integer> integralPowers = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            integralCoefficients.add(coefficients[i] / (powers[i] + 1.0));
            integralPowers.add(powers[i] + 1);
        }

        double[] newCoefficients = new double[integralCoefficients.size()];
        int[] newPowers = new int[integralPowers.size()];

        for (int i = 0; i < integralCoefficients.size(); i++) {
            newCoefficients[i] = integralCoefficients.get(i);
            newPowers[i] = integralPowers.get(i);
        }

        return new Polynomial(newCoefficients, newPowers);
    }

    public Polynomial add(Polynomial other) {
        if (other == null) {
            return this;
        }

        List<Double> resultCoefficients = new ArrayList<>();
        List<Integer> resultPowers = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            resultCoefficients.add(coefficients[i]);
            resultPowers.add(powers[i]);
        }

        for (int i = 0; i < other.coefficients.length; i++) {
            resultCoefficients.add(other.coefficients[i]);
            resultPowers.add(other.powers[i]);
        }

        double[] newCoefficients = new double[resultCoefficients.size()];
        int[] newPowers = new int[resultPowers.size()];

        for (int i = 0; i < resultCoefficients.size(); i++) {
            newCoefficients[i] = resultCoefficients.get(i);
            newPowers[i] = resultPowers.get(i);
        }

        return new Polynomial(newCoefficients, newPowers);
    }

    public Polynomial subtract(Polynomial other) {
        if (other == null) {
            return this;
        }

        List<Double> resultCoefficients = new ArrayList<>();
        List<Integer> resultPowers = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            resultCoefficients.add(coefficients[i]);
            resultPowers.add(powers[i]);
        }

        for (int i = 0; i < other.coefficients.length; i++) {
            resultCoefficients.add(-other.coefficients[i]);
            resultPowers.add(other.powers[i]);
        }

        double[] newCoefficients = new double[resultCoefficients.size()];
        int[] newPowers = new int[resultPowers.size()];

        for (int i = 0; i < resultCoefficients.size(); i++) {
            newCoefficients[i] = resultCoefficients.get(i);
            newPowers[i] = resultPowers.get(i);
        }

        return new Polynomial(newCoefficients, newPowers);
    }

    public Polynomial multiply(Polynomial other) {
        if (other == null) {
            return this;
        }

        List<Double> resultCoefficients = new ArrayList<>();
        List<Integer> resultPowers = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            for (int j = 0; j < other.coefficients.length; j++) {
                resultCoefficients.add(coefficients[i] * other.coefficients[j]);
                resultPowers.add(powers[i] + other.powers[j]);
            }
        }

        double[] newCoefficients = new double[resultCoefficients.size()];
        int[] newPowers = new int[resultPowers.size()];

        for (int i = 0; i < resultCoefficients.size(); i++) {
            newCoefficients[i] = resultCoefficients.get(i);
            newPowers[i] = resultPowers.get(i);
        }

        return new Polynomial(newCoefficients, newPowers);
    }

    public double[] getRoots() {
        if (coefficients.length == 0) {
            return new double[0];
        }

        int highestPower = 0;
        for (int power : powers) {
            if (power > highestPower) {
                highestPower = power;
            }
        }

        if (highestPower == 2) {
            double a = coefficients[0];
            double b = coefficients[1];
            double c = coefficients[2];
            double discriminant = (b * b) - (4.0 * a * c);

            if (discriminant < 0) {
                return new double[0];
            }

            double root1 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
            double root2 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
            return new double[]{root1, root2};
        }

        if (highestPower == 1) {
            double a = coefficients[0];
            double b = coefficients[1];
            if (Math.abs(a) < 1e-12) {
                return new double[0];
            }
            return new double[]{-b / a};
        }

        return new double[0];
    }
}
