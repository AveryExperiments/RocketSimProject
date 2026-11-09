import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

class SimPanel extends JComponent {

    public Graphics2D g;
    ImageIcon imageIcon;
    Graphics2D g2;
    int length;
    int width;
    double angle;
    int x;
    int y;
    int length2;
    int width2;
    double angle2;
    int markDist;
    double xScale;
    double yScl;
    double xMax;
    double yMax;
    List<Point> trajectory = new ArrayList<>();

    public SimPanel() {
        this.setPreferredSize(new java.awt.Dimension(1000, 1000));
        this.setBackground(new Color(42, 44, 52));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        drawBg(g2);
        axesDisplay(g2);
        drawTrajectory(g2);
        drawArrow(x, y, length, width, (int) angle);
        drawArrow(x, y, length2, width2, (int) angle2);
    }

    public void update(int length, int width, int angle, int x, int y, int length2, int width2, int angle2) {
        this.length = length;
        this.width = width;
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.length2 = length2;
        this.width2 = width2;
        this.angle2 = angle2;
        trajectory.add(new Point(x, y));
        repaint();
    }

    public void clearTrajectory() {
        trajectory.clear();
        repaint();
    }

    public void drawTrajectory(Graphics2D g2) {
        if (trajectory.size() < 2) {
            return;
        }
        Color prev = g2.getColor();
        g2.setColor(Color.RED);
        for (int i = 0; i < trajectory.size() - 1; i++) {
            Point p1 = trajectory.get(i);
            Point p2 = trajectory.get(i + 1);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        g2.setColor(prev);
    }

    public void drawBg(Graphics g) {
        Color prev = g.getColor();
        int groundY = Math.max(220, getHeight() - 220);
        int groundHeight = Math.max(80, getHeight() - groundY);
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, groundY, getWidth(), groundHeight);
        g.setColor(new Color(37, 66, 95));
        g.fillRect(0, 0, getWidth(), groundY);
        g.setColor(prev);
    }

    public void drawArrow(int x, int y, int length, int width, int angle) {
        int y2 = (int) (y + length * Math.cos(Math.toRadians(angle + 90)));
        int x2 = (int) (x + length * Math.sin(Math.toRadians(angle + 90)));
        g2.drawLine(x, y, x2, y2);

        if (length == 0) {
            return;
        }

        int[][] pointsPrime = new int[3][2];
        pointsPrime[0][0] = x2;
        pointsPrime[0][1] = y2;
        pointsPrime[1][0] = (int) (pointsPrime[0][0] + 5.0 / 2.0 * 3);
        pointsPrime[1][1] = (int) (pointsPrime[0][1] + 5);
        pointsPrime[2][0] = (int) (pointsPrime[0][0] - 5.0 / 2.0 * 3);
        pointsPrime[2][1] = (int) (pointsPrime[0][1] + 5);

        int rotAngle = (length >= 0) ? (90 - angle) : (270 - angle);

        int x1 = (int) ((pointsPrime[1][0] - pointsPrime[0][0]) * Math.cos(Math.toRadians(rotAngle)) - (pointsPrime[1][1] - pointsPrime[0][1]) * Math.sin(Math.toRadians(rotAngle)) + pointsPrime[0][0]);
        int xDos = (int) ((pointsPrime[2][0] - pointsPrime[0][0]) * Math.cos(Math.toRadians(rotAngle)) - (pointsPrime[2][1] - pointsPrime[0][1]) * Math.sin(Math.toRadians(rotAngle)) + pointsPrime[0][0]);
        int y1 = (int) ((pointsPrime[1][1] - pointsPrime[0][1]) * Math.cos(Math.toRadians(rotAngle)) + (pointsPrime[1][0] - pointsPrime[0][0]) * Math.sin(Math.toRadians(rotAngle)) + pointsPrime[0][1]);
        int yDos = (int) ((pointsPrime[2][1] - pointsPrime[0][1]) * Math.cos(Math.toRadians(rotAngle)) + (pointsPrime[2][0] - pointsPrime[0][0]) * Math.sin(Math.toRadians(rotAngle)) + pointsPrime[0][1]);

        g2.fillPolygon(new int[]{pointsPrime[0][0], x1, xDos}, new int[]{pointsPrime[0][1], y1, yDos}, 3);
    }

    public void axesDisplay(Graphics g) {
        int xOrigin = 60;
        int yOrigin = Math.max(180, getHeight() - 220);
        int xEnd = getWidth() - 60;
        int yTop = 60;
        int markWidth = 6;
        int markDist = Math.max(30, Math.min(50, (xEnd - xOrigin) / 14));

        g.drawLine(xOrigin, yOrigin, xEnd, yOrigin);
        g.drawLine(xOrigin, yTop, xOrigin, yOrigin);

        int curNumber = 0;
        for (int i = xOrigin; i <= xEnd; i += markDist) {
            g.drawLine(i, yOrigin, i, yOrigin + markWidth);
            g.drawString("" + curNumber, i - 10, yOrigin + 20);
            curNumber += markDist;
        }

        curNumber = 0;
        for (int j = yOrigin; j >= yTop; j -= markDist) {
            g.drawLine(xOrigin - markWidth, j, xOrigin, j);
            g.drawString("" + curNumber, xOrigin - 35, j + 5);
            curNumber += markDist;
        }
    }
}
