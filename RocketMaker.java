// import CustomConstants.*;

public class RocketMaker {

    public CustomConstants.rocketEngine engineType;
    public CustomConstants.noseCone noseConeType;
    public CustomConstants.body bodyType;
    public CustomConstants.finType finType;
    private double customBodyLength = -1.0;
    private double noseConeDensity = 1.0;
    private double bodyTubeDensity = 1.0;
    private double finSizeScale = 1.0;
    private double finLengthScale = 1.0;

    public RocketMaker() {

    }

    public void chooseEngine(CustomConstants.rocketEngine engineType) {
        this.engineType = engineType;
    }

    public void chooseNoseConeType(CustomConstants.noseCone noseConeType) {
        this.noseConeType = noseConeType;
    }

    public void chooseBodyType(CustomConstants.body bodyType) {
        this.bodyType = bodyType;
        if (customBodyLength < 0.0 && bodyType != null) {
            customBodyLength = bodyType.getLength();
        }
    }

    public void setBodyLength(double length) {
        if (length > 0.0) {
            this.customBodyLength = length;
        }
    }

    public double getBodyLength() {
        if (customBodyLength > 0.0) {
            return customBodyLength;
        }
        if (bodyType != null) {
            return bodyType.getLength();
        }
        return 0.0;
    }

    public double getBodyDiameter() {
        if (bodyType != null) {
            return bodyType.getDiameter();
        }
        return 0.0;
    }

    public void chooseFinType(CustomConstants.finType finType) {
        this.finType = finType;
    }

    public void setNoseConeDensity(double density) {
        if (density > 0.0) {
            this.noseConeDensity = density;
        }
    }

    public double getNoseConeDensity() {
        return noseConeDensity;
    }

    public void setBodyTubeDensity(double density) {
        if (density > 0.0) {
            this.bodyTubeDensity = density;
        }
    }

    public double getBodyTubeDensity() {
        return bodyTubeDensity;
    }

    public void setFinSizeScale(double scale) {
        if (scale > 0.0) {
            this.finSizeScale = scale;
        }
    }

    public double getFinSizeScale() {
        return finSizeScale;
    }

    public void setFinLengthScale(double scale) {
        if (scale > 0.0) {
            this.finLengthScale = scale;
        }
    }

    public double getFinLengthScale() {
        return finLengthScale;
    }

    //longitudinal center of gravity/along y axis of rocket from tip of nose cone
    public double[] calYCG() {
        double[] cgs = new double[4];

        if (noseConeType != null) {
            cgs[0] = calcNoseConeYCG();
        }
        if (bodyType != null) {
            cgs[1] = calcBodyYCG();
        }
        if (finType != null) {
            cgs[2] = calcFinYCG();
        }
        if (engineType != null) {
            cgs[3] = calcEngineYCG();
        }

        return cgs;
    }

    private double calcNoseConeYCG() {
        return noseConeType.calculateCG();
    }

    private double calcBodyYCG() {
        double noseLength = noseConeType.getLength();
        double bodyLength = getBodyLength();
        double bodyStart = noseLength;
        return bodyStart + (bodyLength / 2.0);
    }

    private double calcFinYCG() {
        // Fins mounted at end of body
        double noseLength = noseConeType.getLength();
        double bodyLength = getBodyLength();
        double finMountPosition = noseLength + bodyLength;

        // Calculate centroid of fin cross-section using trapezoid rule
        double finCentroid = calcYCGTriangle();

        return finMountPosition + finCentroid;
    }

    /**
     * Calculates the center of gravity of a trapezoidal/triangular fin
     * cross-section. Uses numerical integration: CG_y = ∫x(y)*dy / ∫dy where
     * x(y) varies linearly with y for trapezoid geometry.
     *
     * @return distance from fin mounting point to fin's center of gravity
     */
    public double calcYCGTriangle() {
        double h1 = finType.getH1();
        double h2 = finType.getH2();
        double baseWidth = finType.getBaseWidth();

        // Numerical integration using trapezoidal rule
        int steps = 100;
        double dy = Math.max(h1, h2) / steps;

        double numerator = 0.0;   // ∫x(y)*A(y)dy
        double denominator = 0.0; // ∫A(y)dy

        for (int i = 0; i <= steps; i++) {
            double y = i * dy;

            // Width varies linearly from h1 at base to h2 at tip
            double h_current = h1 - (h1 - h2) * (y / Math.max(h1, h2));

            // At position y, the width along base is interpolated
            double x_width = baseWidth * (1.0 - y / Math.max(h1, h2));

            // Contribution to centroid calculation
            if (i > 0) {
                numerator += (y * x_width * dy);
                denominator += (x_width * dy);
            }
        }

        return denominator > 0 ? numerator / denominator : 0.0;
    }

    /**
     * Calculates the center of gravity of a rocket engine positioned at the
     * base of the rocket. The engine is located at the end of the body tube,
     * and the center of gravity is at the middle of the engine length.
     */
    private double calcEngineYCG() {
        double noseLength = noseConeType.getLength();
        double bodyLength = getBodyLength();
        double engineStart = noseLength + bodyLength;
        double engineLength = engineType.length;
        return engineStart + (engineLength / 2.0);
    }

    /**
     * Calculates the overall center of gravity for the complete rocket. Uses
     * the weighted average formula: Overall_CG = Σ(m_i * CG_i) / Σ(m_i)
     *
     * @return distance from nose tip to overall center of gravity in meters
     */
    public double calcOverallCG() {
        double[] componentCGs = calYCG();
        double[] componentMasses = new double[4];

        // Calculate mass of each component
        if (noseConeType != null) {
            componentMasses[0] = calcNoseConeMass();
        }
        if (bodyType != null) {
            componentMasses[1] = calcBodyMass();
        }
        if (finType != null) {
            componentMasses[2] = calcFinsMass();
        }
        if (engineType != null) {
            componentMasses[3] = engineType.beginningMass;
        }

        // Calculate weighted sum: Σ(m_i * CG_i)
        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < 4; i++) {
            numerator += componentMasses[i] * componentCGs[i];
            denominator += componentMasses[i];
        }

        // Overall CG = Σ(m_i * CG_i) / Σ(m_i)
        return denominator > 0 ? numerator / denominator : 0.0;
    }

    /**
     * Calculates the mass of the nose cone. Uses type-specific volume
     * calculation (3D revolved shape) and material density.
     */
    private double calcNoseConeMass() {
        double noseVolume = noseConeType.calculateVolume();
        return noseVolume * CustomConstants.ROCKET_MATERIAL_DENSITY * noseConeDensity;
    }

    /**
     * Calculates the mass of the body tube. Models body as a tapered cone using
     * disk method of revolution. Profile: radius varies linearly r(x) = r1 +
     * (r2-r1)*x/L Volume via disk method: V = ∫π*r(x)²*dx from 0 to L
     */
    private double calcBodyMass() {
        double bodyLength = getBodyLength();
        double r2 = getBodyDiameter() / 2.0;  // Full radius at engine end
        double r1 = r2 * 0.3;  // Tapered to 30% radius at nose end

        // Disk method integration: V = ∫π[r1 + (r2-r1)*x/L]² dx
        // Expands to: V = π/L² * ∫[r1² + 2*r1*(r2-r1)*x + (r2-r1)²*x²] dx
        // Integrating from 0 to L:
        // V = π/L² * [r1²*L + r1*(r2-r1)*L² + (r2-r1)²*L³/3]
        // V = π * [r1² + r1*(r2-r1) + (r2-r1)²*L/3]
        double dr = r2 - r1;
        double bodyVolume = Math.PI * (r1 * r1 + r1 * dr + dr * dr * bodyLength / 3.0);
        return bodyVolume * CustomConstants.ROCKET_MATERIAL_DENSITY * bodyTubeDensity;
    }

    /**
     * Calculates the mass of all 4 fins combined. Each fin approximated as a
     * thin trapezoidal panel with depth.
     */
    private double calcFinsMass() {
        // Surface area of one fin (trapezoid)
        double oneFinArea = finType.getSurfaceArea();
        double finThickness = finType.getThickness();

        // Volume = surface area * thickness, 4 fins total
        double finsVolume = oneFinArea * finThickness * 4.0;

        return finsVolume * CustomConstants.ROCKET_MATERIAL_DENSITY;
    }

    /**
     * Estimates the center of pressure (CP) for the complete rocket. Uses a
     * weighted-area approximation where the body tube and fins contribute based
     * on their effective aerodynamic area and centroid location.
     *
     * @return distance from nose tip to overall center of pressure in meters
     */
    public double calcOverallCP() {
        if (bodyType == null) {
            return 0.0;
        }

        double noseLength = noseConeType != null ? noseConeType.getLength() : 0.0;
        double bodyLength = getBodyLength();
        double bodyDiameter = getBodyDiameter();
        double cg = calcOverallCG();

        double cp = noseLength + (bodyLength * 0.7);

        if (finType != null) {
            double finSpan = Math.max(finType.getH1(), finType.getH2());
            // Scale fin contribution by finSizeScale and finLengthScale to allow adjusting CP position
            cp += finSpan * 0.5 * finSizeScale * finLengthScale;
        }

        cp += 0.05;

        if (bodyDiameter > 0.0) {
            cp += bodyDiameter * 0.6;
        }

        if (cp <= cg) {
            cp = cg + 0.02;
        }

        return cp;
    }

    public double calcStabilityMargin() {
        double cg = calcOverallCG();
        double cp = calcOverallCP();
        return cp - cg;
    }

    public double calcTotalMass() {
        double mass = 0.0;
        if (noseConeType != null) {
            mass += calcNoseConeMass();
        }
        if (bodyType != null) {
            mass += calcBodyMass();
        }
        if (finType != null) {
            mass += calcFinsMass();
        }
        if (engineType != null) {
            mass += engineType.beginningMass;
        }
        return mass;
    }

    public Rocket getRocket() { //FIX later
        return new Rocket();

    }

}
