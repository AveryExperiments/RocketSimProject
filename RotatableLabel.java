
import java.awt.*;
import javax.swing.*;

public class RotatableLabel extends JLabel {

    private double angleDegrees = 0;

    public RotatableLabel(ImageIcon icon) {
        super(icon);
    }

    public void setAngle(double degrees) {
        this.angleDegrees = degrees;
        repaint();
    }

    public double getAngle() {
        return angleDegrees;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Cast to Graphics2D to unlock advanced geometry operations
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother image rendering during rotation
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Calculate the center coordinates of the label
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        double radians = Math.toRadians(angleDegrees);
        g2d.rotate(radians, centerX, centerY);

        // Let the standard JLabel drawing logic execute inside the rotated context
        super.paintComponent(g2d);

        // Always dispose graphics copies to free system resources
        g2d.dispose();
    }
}
