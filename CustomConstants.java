
public class CustomConstants {

    public enum rocketEngine {
        // got these from estes data sheets
        A8_3("A8-3", 2.50, 0.50, 0.018, 0.050, 0.0156, 0.0041),
        B6_4("B6-4", 5.00, 0.80, 0.018, 0.070, 0.0179, 0.0065);

        public final String engineCode;
        public final double totalImpulse;   // (N-s)
        public final double burnTime;       // (s)
        public final double diameter;       // (m)
        public final double length;         // (m)
        public final double beginningMass;  // (kg) 
        public final double propellantMass; // (kg) 
        public final double dryMass;        // Kilograms (kg) 

        rocketEngine(String engineCode, double totalImpulse, double burnTime, double diameter, double length, double beginningMass, double propellantMass) {
            this.engineCode = engineCode;
            this.totalImpulse = totalImpulse;
            this.burnTime = burnTime;
            this.diameter = diameter;
            this.length = length;
            this.beginningMass = beginningMass;
            this.propellantMass = propellantMass;

            this.dryMass = beginningMass - propellantMass;
        }
    }

    public enum body {
        Standard(0.4, 0.019, 0.001); // length, diameter, wall thickness
        private final double length;
        private final double diameter;
        private final double wallThickness;

        body(double length, double diameter, double wallThickness) {
            this.length = length;
            this.diameter = diameter;
            this.wallThickness = wallThickness;
        }

        public double getLength() {
            return this.length;
        }

        public double getDiameter() {
            return this.diameter;
        }

        public double getWallThickness() {
            return this.wallThickness;
        }
    }

    public enum noseCone {
        Cone(0.019, 0.020), // baseWidth, length
        RoundedCone(0.019, 0.020), // baseWidth, length
        Flat(0.019, 0.005);           // baseWidth, length (flat has minimal length)

        private final double baseWidth;
        private final double length;

        noseCone(double baseWidth, double length) {
            this.baseWidth = baseWidth;
            this.length = length;
        }

        public double getBaseWidth() {
            return this.baseWidth;
        }

        public double getLength() {
            return this.length;
        }

        /**
         * Calculates the center of gravity location for this nose cone type.
         * Different cone geometries have different centroid positions.
         *
         * @return distance from tip of nose cone to its center of gravity
         */
        public double calculateCG() {
            switch (this) {
                case Cone:
                    // Right cone CG: L/4 from tip
                    return this.length / 4.0;
                case RoundedCone:
                    // Rounded cone CG: approximately L/3 from tip
                    return this.length / 3.0;
                case Flat:
                    // Flat plate CG: at L/2 from tip
                    return this.length / 2.0;
                default:
                    return this.length / 4.0;
            }
        }

        /**
         * Calculates the volume of this nose cone type.
         *
         * @return volume in cubic meters
         */
        public double calculateVolume() {
            double radius = this.baseWidth / 2.0;
            switch (this) {
                case Cone:
                    // Cone volume
                    return (1.0 / 3.0) * Math.PI * radius * radius * this.length;
                case RoundedCone:
                    // Rounded cone/basically a parabola
                    return (1.0 / 2.0) * Math.PI * radius * radius * this.length;
                case Flat:
                    // Flat plate volume super thin
                    return Math.PI * radius * radius * 0.001; // ~1mm thick
                default:
                    return (1.0 / 3.0) * Math.PI * radius * radius * this.length;
            }
        }
    }

    public enum finType {

        RECTANGLE(0.050, 0.050, 0.040, 0.003),
        TRAPEZOID(0.060, 0.030, 0.045, 0.003),
        TRIANGLE(0.055, 0.000, 0.040, 0.003);

        private final double h1;
        private final double h2;
        private final double baseWidth;
        private final double thickness;

        finType(double h1, double h2, double baseWidth, double thickness) {
            this.h1 = h1;
            this.h2 = h2;
            this.baseWidth = baseWidth;
            this.thickness = thickness;
        }

        // Standard getters to safely read your private variables
        public double getH1() {
            return this.h1;
        }

        public double getH2() {
            return this.h2;
        }

        public double getBaseWidth() {
            return this.baseWidth;
        }

        public double getThickness() {
            return this.thickness;
        }

        /**
         * Calculates the surface area of one side of the fin using your exact
         * variables.
         */
        public double getSurfaceArea() {
            return ((this.h1 + this.h2) / 2.0) * this.baseWidth;
        }
    }
    public static final double ROCKET_MATERIAL_DENSITY = 150.0;  // kg/m³  complete guesstimate

    public CustomConstants() {
    }
}
