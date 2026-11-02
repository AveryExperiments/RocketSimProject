public class CustomRocket {

    private RocketMaker rocketMaker;
    private double fuelMass;
    private double fuelBurnTime;
    private double launchAngle;
    private double initialVelocity;
    private double totalImpulse;
    private double rocketMass;
    private double cd;
    private double area;
    private double time;

    public CustomRocket(RocketMaker rocketMaker) {
        this.rocketMaker = (rocketMaker != null) ? rocketMaker : new RocketMaker();
        this.fuelMass = 0.02;
        this.fuelBurnTime = 1.2;
        this.launchAngle = 20.0;
        this.initialVelocity = 0.0;
        this.totalImpulse = 8.0;
        this.rocketMass = this.rocketMaker.calcTotalMass();
        this.time = 20.0;
        this.area = computeReferenceArea();
        this.cd = calcDragCoefficient(this.launchAngle);
    }

    public CustomRocket() {
        this(new RocketMaker());
    }

    private double computeReferenceArea() {
        if (rocketMaker == null) {
            return Math.PI * 0.0127 * 0.0127;
        }
        double diameter = Math.max(0.01, rocketMaker.getBodyDiameter());
        double radius = diameter / 2.0;
        return Math.PI * radius * radius;
    }

    public double calcDragCoefficient(double theta) {
        if (rocketMaker == null) {
            return 0.5;
        }

        double bodyLength = Math.max(0.05, rocketMaker.getBodyLength());
        double bodyDiameter = Math.max(0.01, rocketMaker.getBodyDiameter());
        double radius = bodyDiameter / 2.0;
        double frontalArea = Math.PI * radius * radius;
        double wettedArea = Math.PI * bodyDiameter * bodyLength + 2.0 * Math.PI * radius * bodyLength;

        double speed = Math.max(5.0, initialVelocity + 20.0);
        double reynolds = (1.225 * speed * bodyLength) / 0.000018;
        double skinFriction = 1.328 / Math.sqrt(Math.max(reynolds, 1.0));

        double guess = 0.5;
        double lastCd = guess;

        for (int i = 0; i < 100; i++) {
            double skinDrag = skinFriction * (wettedArea / Math.max(frontalArea, 0.0001));
            double angleFactor = 0.05 + (Math.abs(theta) / 90.0) * 0.12;
            double totalDrag = skinDrag + angleFactor + 0.18;
            double error = totalDrag - lastCd;
            lastCd = totalDrag;
            if (Math.abs(error) < 0.01) {
                this.cd = totalDrag;
                return totalDrag;
            }
        }

        this.cd = lastCd;
        return lastCd;
    }

    public double[] launch(double theta) {
        this.launchAngle = theta;
        this.cd = calcDragCoefficient(theta);
        this.area = computeReferenceArea();

        RocketMath rocketMath = new RocketMath(new double[]{0, 0}, new double[]{0, 0}, new double[]{0, 0});
        double radius = Math.max(0.0127, (rocketMaker != null && rocketMaker.getBodyDiameter() > 0) ? rocketMaker.getBodyDiameter() / 2.0 : 0.0254);
        double area = Math.PI * radius * radius;

        return rocketMath.calcPosition(
                fuelMass,
                fuelBurnTime,
                launchAngle,
                initialVelocity,
                totalImpulse,
                rocketMass,
                cd,
                area,
                time
        );
    }

    public void setFuelMass(double fuelMass) {
        this.fuelMass = fuelMass;
    }

    public void setFuelBurnTime(double fuelBurnTime) {
        this.fuelBurnTime = fuelBurnTime;
    }

    public void setLaunchAngle(double launchAngle) {
        this.launchAngle = launchAngle;
    }

    public void setInitialVelocity(double initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public void setTotalImpulse(double totalImpulse) {
        this.totalImpulse = totalImpulse;
    }

    public void setRocketMass(double rocketMass) {
        this.rocketMass = rocketMass;
    }

    public void setCd(double cd) {
        this.cd = cd;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public RocketMaker getRocketMaker() {
        return rocketMaker;
    }

    public double getFuelMass() {
        return fuelMass;
    }

    public double getFuelBurnTime() {
        return fuelBurnTime;
    }

    public double getLaunchAngle() {
        return launchAngle;
    }

    public double getInitialVelocity() {
        return initialVelocity;
    }

    public double getTotalImpulse() {
        return totalImpulse;
    }

    public double getRocketMass() {
        return rocketMass;
    }

    public double getCd() {
        return cd;
    }

    public double getArea() {
        return area;
    }

    public double getTime() {
        return time;
    }
}
