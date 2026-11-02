import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

class CustomizationPanel extends JComponent {

    Graphics2D g2;
    private RocketMaker rocketMaker;
    private static final int SCALE = 900;  // much larger view for the rocket model
    private static final int START_X = 320;
    private static final int START_Y = 40;

    public CustomizationPanel(RocketMaker rocketMaker) {
        this.rocketMaker = rocketMaker;
        this.setPreferredSize(new java.awt.Dimension(1000, 1000));
        this.setBackground(new Color(42, 44, 52));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));

        if (rocketMaker != null) {
            drawRocket();
            drawCenterOfGravity();
            drawCenterOfPressure();
            drawStabilityMarker();
        }
    }

    private void drawRocket() {
        if (rocketMaker.noseConeType != null) {
            drawNoseCone();
        }
        if (rocketMaker.bodyType != null) {
            drawBodyTube();
        }
        if (rocketMaker.finType != null) {
            drawFin();
        }
        if (rocketMaker.engineType != null) {
            drawMotor();
        }
    }

    public void drawBodyTube() {
        if (rocketMaker.noseConeType == null || rocketMaker.bodyType == null) return;

        double noseLength = rocketMaker.noseConeType.getLength();
        double bodyLength = rocketMaker.getBodyLength();
        double bodyDiameter = rocketMaker.bodyType.getDiameter();

        int nosePixels = (int) (noseLength * SCALE);
        int bodyPixels = (int) (bodyLength * SCALE);
        int bodyWidthPixels = (int) (bodyDiameter * SCALE);

        int startX = START_X - bodyWidthPixels / 2;
        int startY = START_Y + nosePixels;

        g2.setColor(Color.GRAY);
        g2.fillRoundRect(startX, startY, bodyWidthPixels, bodyPixels, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(startX, startY, bodyWidthPixels, bodyPixels, 12, 12);
    }

    public void drawNoseCone() {
        if (rocketMaker.noseConeType == null) return;

        double noseLength = rocketMaker.noseConeType.getLength();
        double noseWidth = rocketMaker.noseConeType.getBaseWidth();

        int nosePixels = (int) (noseLength * SCALE);
        int noseWidthPixels = (int) (noseWidth * SCALE);

        int tipX = START_X;
        int tipY = START_Y;
        int baseX1 = START_X - noseWidthPixels / 2;
        int baseX2 = START_X + noseWidthPixels / 2;
        int baseY = START_Y + nosePixels;

        g2.setColor(new Color(200, 100, 100));

        switch (rocketMaker.noseConeType) {
            case Cone:
                int[] xPoints = {tipX, baseX1, baseX2};
                int[] yPoints = {tipY, baseY, baseY};
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.setColor(Color.BLACK);
                g2.drawPolygon(xPoints, yPoints, 3);
                break;
            case RoundedCone:
                g2.fillArc(baseX1, tipY, noseWidthPixels, nosePixels, 0, 180);
                g2.setColor(Color.BLACK);
                g2.drawArc(baseX1, tipY, noseWidthPixels, nosePixels, 0, 180);
                g2.drawLine(baseX1, baseY, tipX, tipY);
                g2.drawLine(baseX2, baseY, tipX, tipY);
                break;
            case Flat:
                g2.fillRect(baseX1, tipY, noseWidthPixels, 8);
                g2.setColor(Color.BLACK);
                g2.drawRect(baseX1, tipY, noseWidthPixels, 8);
                break;
        }
    }

    public void drawFin() {
        if (rocketMaker.finType == null || rocketMaker.noseConeType == null || rocketMaker.bodyType == null) return;

        double noseLength = rocketMaker.noseConeType.getLength();
        double bodyLength = rocketMaker.getBodyLength();
        double bodyDiameter = rocketMaker.bodyType.getDiameter();
        double h1 = rocketMaker.finType.getH1() * rocketMaker.getFinSizeScale() * rocketMaker.getFinLengthScale();
        double h2 = rocketMaker.finType.getH2() * rocketMaker.getFinSizeScale() * rocketMaker.getFinLengthScale();
        double baseWidth = rocketMaker.finType.getBaseWidth();

        int finStartY = (int) ((noseLength + bodyLength) * SCALE) + START_Y;
        int bodyWidthPixels = (int) (bodyDiameter * SCALE);
        int h1Pixels = (int) (h1 * SCALE);
        int h2Pixels = (int) (h2 * SCALE);
        int baseWidthPixels = (int) (baseWidth * SCALE);

        int leftBaseX = START_X - bodyWidthPixels / 2;
        int rightBaseX = START_X + bodyWidthPixels / 2;

        g2.setColor(new Color(150, 150, 200));

        if (rocketMaker.finType == CustomConstants.finType.RECTANGLE) {
            int[] leftXPoints = {
                    leftBaseX,
                    leftBaseX,
                    leftBaseX - h1Pixels,
                    leftBaseX - h1Pixels
            };
            int[] leftYPoints = {
                    finStartY,
                    finStartY + baseWidthPixels,
                    finStartY + baseWidthPixels,
                    finStartY
            };
            g2.fillPolygon(leftXPoints, leftYPoints, 4);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(leftXPoints, leftYPoints, 4);

            g2.setColor(new Color(150, 150, 200));
            int[] rightXPoints = {
                    rightBaseX,
                    rightBaseX,
                    rightBaseX + h1Pixels,
                    rightBaseX + h1Pixels
            };
            int[] rightYPoints = {
                    finStartY,
                    finStartY + baseWidthPixels,
                    finStartY + baseWidthPixels,
                    finStartY
            };
            g2.fillPolygon(rightXPoints, rightYPoints, 4);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(rightXPoints, rightYPoints, 4);
            return;
        }

        int[] leftXPoints = {
                leftBaseX,
                leftBaseX,
                leftBaseX - h2Pixels,
                leftBaseX - h1Pixels
        };
        int[] leftYPoints = {
                finStartY + baseWidthPixels,
                finStartY,
                finStartY + (int) (baseWidthPixels * 0.10),
                finStartY + (int) (baseWidthPixels * 0.75)
        };
        g2.fillPolygon(leftXPoints, leftYPoints, 4);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(leftXPoints, leftYPoints, 4);

        g2.setColor(new Color(150, 150, 200));
        int[] rightXPoints = {
                rightBaseX,
                rightBaseX,
                rightBaseX + h2Pixels,
                rightBaseX + h1Pixels
        };
        int[] rightYPoints = {
                finStartY + baseWidthPixels,
                finStartY,
                finStartY + (int) (baseWidthPixels * 0.10),
                finStartY + (int) (baseWidthPixels * 0.75)
        };
        g2.fillPolygon(rightXPoints, rightYPoints, 4);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(rightXPoints, rightYPoints, 4);
    }

    public void drawMotor() {
        if (rocketMaker.engineType == null || rocketMaker.noseConeType == null || rocketMaker.bodyType == null) return;

        double noseLength = rocketMaker.noseConeType.getLength();
        double bodyLength = rocketMaker.getBodyLength();
        double bodyDiameter = rocketMaker.bodyType.getDiameter();
        double engineLength = rocketMaker.engineType.length;
        double engineDiameter = rocketMaker.engineType.diameter;

        int motorStartY = (int) ((noseLength + bodyLength) * SCALE) + START_Y;
        int motorEndY = (int) ((noseLength + bodyLength + engineLength) * SCALE) + START_Y;
        int motorWidthPixels = (int) (engineDiameter * SCALE);

        int motorX = START_X - motorWidthPixels / 2;
        int motorY = motorStartY;
        int motorHeight = motorEndY - motorStartY;

        g2.setColor(new Color(255, 100, 0));
        g2.fillRect(motorX, motorY, motorWidthPixels, motorHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(motorX, motorY, motorWidthPixels, motorHeight);
    }

    private void drawCenterOfGravity() {
        if (rocketMaker.noseConeType == null || rocketMaker.bodyType == null) {
            return;
        }

        double overallCG = rocketMaker.calcOverallCG();
        int cgX = START_X;
        int cgY = START_Y + (int) (overallCG * SCALE);

        g2.setColor(Color.RED);
        g2.fillOval(cgX - 6, cgY - 6, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawOval(cgX - 6, cgY - 6, 12, 12);
    }

    private void drawCenterOfPressure() {
        if (rocketMaker.noseConeType == null || rocketMaker.bodyType == null) {
            return;
        }

        double overallCP = rocketMaker.calcOverallCP();
        int cpX = START_X;
        int cpY = START_Y + (int) (overallCP * SCALE);

        g2.setColor(Color.BLUE);
        g2.fillOval(cpX - 5, cpY - 5, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawOval(cpX - 5, cpY - 5, 10, 10);
    }

    private void drawStabilityMarker() {
        if (rocketMaker.noseConeType == null || rocketMaker.bodyType == null) {
            return;
        }

        double cg = rocketMaker.calcOverallCG();
        double cp = rocketMaker.calcOverallCP();
        boolean stable = cg < cp;

        g2.setColor(stable ? new Color(0, 170, 0) : new Color(180, 0, 0));
        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
        g2.drawString(stable ? "STABLE" : "UNSTABLE", START_X + 120, START_Y + 30);
    }

    public void update() {
        repaint();
    }
}
