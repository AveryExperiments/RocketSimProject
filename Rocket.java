
import javax.swing.JPanel;

public class Rocket {

    // public int rocketSize;
    public RocketConstants constants;
    private double[] dimensions;
    private double rocketWeight;
    public RocketConstants.rocketModel rocketModel;
    double launchAngle;

    public Rocket() {
        this.constants = new RocketConstants();
        dimensions = new double[6];

        // RocketConstants.rocketModel model = constants.rocketModel.getRocketModel();
        // this.constants.setRocketmodel(model);
        // this.rocketSize = s;
        // if (s <= 0) {
        //     s = 1;
        // }
        // constants.height = dimensions[2];
        // constants.width = dimensions[3];
        // constants.weight = 1000;
        // constants.fuel = 0;
        // for (int i = 0; i < dimensions.length; i++) {
        //     dimensions[i] = dimensions[i] * s;
        // }
    }

    public void setModel(RocketConstants.rocketModel model) {
        this.rocketModel = model;
    }

    public double[] launch(JPanel panel) {
        System.out.println("Executing on thread: " + Thread.currentThread().getName());
        RocketMath rocketMath = new RocketMath(new double[]{0, 0}, new double[]{0, 0}, new double[]{0, 0});
        double[] position = new double[7];
        double radius = (this.rocketModel != null ? this.rocketModel.width : 0.0254) / 2.0;
        double area = Math.PI * radius * radius;
        double cd = (this.rocketModel != null ? this.rocketModel.cd : 0.60);
        double weight = (this.rocketModel != null ? this.rocketModel.weight : 0.7);
        position = rocketMath.calcPosition(0.0242, 1.7, launchAngle, 0, 50, weight, cd, area, 20);
        return position;

        // int delay = 200; // 500 milliseconds between updates
        // int speedFactor = this.constants.fuel * 5;
        // Timer timer = new Timer(delay, null); // Initialize timer
        // timer.addActionListener((ActionEvent e) -> {
        //     // 1. Update your graphics data
        //     dimensions[1] = dimensions[1] - speedFactor * 2;
        //     panel.update(dimensions);
        //     // 3. Check your exit condition (instead of a while-loop condition)
        //     if (dimensions[1] < -200) {
        //         timer.stop(); // This cleanly exits and destroys the timer
        //         System.out.println("Animation complete. Timer stopped.");
        //     }
        // });
        // timer.start();
    }

    public void updateDimensions(double[] newDimensions) {
        this.dimensions = newDimensions;
    }

    public void setLaunch(int angle) {
        this.launchAngle = angle;
    }

    public void addFuelMass(int fuel) {
        this.constants.fuel += fuel; //in kg
    }

    // public void launch() {
    //     rocket.paintComponent(rocket.g);
    // }
}
