
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RocketMath {

    final double gravity = 9.81;
    final double airDensity = 1.225; // kg/m^3 air density at sea level
    private double cd; // Drag coefficient
    private double area; // Cross-sectional area in m^2
    private double dragForceX;
    private double dragForceY;
    private double thrustY;
    private double thrustX;
    private double fuelMass;
    private double massFlowRate;
    private double exhaustVelocity;
    private double launchAngle;
    private double time;
    private double accelerationX;
    private double accelerationY;

    Path path = Path.of("points.txt");

    public RocketMath(double[] initialPosition, double[] velocity, double[] acceleration) {

        try {
            Files.deleteIfExists(path);
            Files.createFile(path);

        } catch (IOException e) {
            System.out.println(e);
        }

        this.time = time;
    }

    public double[] calcPosition(double fuelMass, double fuelBurnTime, double launchAngle, double initialVelocity, double totalImpulse, double rocketMass, double cd, double area, double time) {
        StringBuilder points = new StringBuilder();
        points.append(0).append(" ,").append(0).append(", ").append(launchAngle)
                .append(", ").append(initialVelocity * Math.cos(Math.toRadians(launchAngle)))
                .append(", ").append(initialVelocity * Math.sin(Math.toRadians(launchAngle))).append("\n");

        this.fuelMass = fuelMass;
        this.cd = cd;
        this.area = area;
        double velocityX = initialVelocity * Math.cos(Math.toRadians(launchAngle));
        double velocityY = initialVelocity * Math.sin(Math.toRadians(launchAngle));
        double maxVelocity = 0;
        double heading = launchAngle;
        double x = 0;
        double y = 0;
        double dryMassFuel = 0.017;
        double newFuelMass = fuelMass;
        double[] finalPosition = new double[7];
        this.massFlowRate = fuelMass / fuelBurnTime;
        double exhaustVelocity = totalImpulse / fuelMass;
        this.thrustY = (massFlowRate * exhaustVelocity) * (Math.sin(Math.toRadians(launchAngle)));
        this.thrustX = (massFlowRate * exhaustVelocity) * (Math.cos(Math.toRadians(launchAngle)));
        double deltaTime = 0.05;
        double totTime = 0;
        double greatestY = 0;
        double greatestX = 0;

        for (double i = 0; i < time; i += deltaTime) {
            newFuelMass = newFuelMass - (massFlowRate * deltaTime);
            double currentFuel = Math.max(0, newFuelMass);
            double totalMass = currentFuel + dryMassFuel + rocketMass;

            double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            double dragForce = 0.5 * airDensity * cd * area * speed * speed;
            double dragAx = (speed > 0) ? (-dragForce * (velocityX / speed) / totalMass) : 0;
            double dragAy = (speed > 0) ? (-dragForce * (velocityY / speed) / totalMass) : 0;

            if (newFuelMass > 0) {
                this.accelerationX = (thrustX / totalMass) + dragAx;
                this.accelerationY = (thrustY / totalMass) - gravity + dragAy;
            } else {
                this.accelerationX = dragAx;
                this.accelerationY = -gravity + dragAy;
            }

            x = x + (velocityX * deltaTime) + (0.5 * (accelerationX * deltaTime * deltaTime));
            if (x > greatestX) {
                greatestX = x;
            }
            if (speed > maxVelocity) {
                maxVelocity = speed;
            }

            y = y + (velocityY * deltaTime) + (0.5 * (accelerationY * deltaTime * deltaTime));
            if (y > greatestY) {
                greatestY = y;
            }

            velocityX = velocityX + deltaTime * accelerationX;
            velocityY = velocityY + deltaTime * accelerationY;
            double rawAngle = Math.toDegrees(Math.atan2(velocityY, velocityX));
            heading = (rawAngle < 0) ? (rawAngle + 360) : rawAngle;
            totTime = totTime + deltaTime;

            points.append(x).append(" ,").append(y).append(", ").append(heading)
                    .append(", ").append(velocityX).append(", ").append(velocityY).append("\n");

            if (y < 0 && totTime > fuelBurnTime) {
                y = 0;
                heading = 0;
                points.append(x).append(" ,").append(y).append(", ").append(heading)
                        .append(", ").append(velocityX).append(", ").append(velocityY).append("\n");
                break;
            }
        }

        try {
            Files.writeString(path, points.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println(e);
        }

        finalPosition[0] = x;
        finalPosition[1] = y;
        finalPosition[2] = greatestX;
        finalPosition[3] = greatestY;
        finalPosition[4] = totTime;
        finalPosition[5] = deltaTime;
        finalPosition[6] = maxVelocity;
        return finalPosition;
    }

    public double[] calcPosition(double fuelMass, double fuelBurnTime, double launchAngle, double initialVelocity, double totalImpulse, double rocketMass, double time) {
        return calcPosition(fuelMass, fuelBurnTime, launchAngle, initialVelocity, totalImpulse, rocketMass, 0.5, 0.001, time);
    }
}
