import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class RoundButton extends JButton {
    private Color buttonColor = new Color(166, 225, 250);
    private Color hoverColor = new Color(186, 235, 255);
    private Color pressedColor = new Color(146, 205, 230);
    private Color textColor = new Color(25, 25, 35);
    private int radius = 20;

    public RoundButton() {
        this("Button");
    }

    public RoundButton(String text) {
        this(text, new Color(166, 225, 250), 20);
    }

    public RoundButton(String text, int radius) {
        this(text, new Color(166, 225, 250), radius);
    }

    public RoundButton(String text, Color buttonColor, int radius) {
        this(text, buttonColor, new Color(25, 25, 35), new Font("SansSerif", Font.BOLD, 14), radius);
    }

    public RoundButton(String text, Color buttonColor, Color textColor, Font font, int radius) {
        super(text);
        this.buttonColor = buttonColor;
        this.textColor = textColor;
        this.radius = radius;
        setFont(font);
        setForeground(textColor);
        setBackground(buttonColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        this.buttonColor = bg;
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        this.textColor = fg;
        super.setForeground(fg);
        repaint();
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(0, radius);
        repaint();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fillColor = buttonColor;
        if (getModel().isPressed()) {
            fillColor = pressedColor;
        } else if (getModel().isRollover()) {
            fillColor = hoverColor;
        }

        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(fillColor);
        g2.fill(shape);

        Icon icon = getIcon();
        if (icon != null) {
            int iconX = (getWidth() - icon.getIconWidth()) / 2;
            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, iconX, iconY);
        }

        String text = getText();
        if (text != null && !text.trim().isEmpty()) {
            String plainText = text.replaceAll("<.*?>", "");
            if (!plainText.isEmpty()) {
                g2.setColor(getForeground());
                int textWidth = g2.getFontMetrics().stringWidth(plainText);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
                g2.drawString(plainText, x, y);
            }
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        g2.dispose();
    }
}
