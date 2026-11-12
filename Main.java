import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main {

    private static ImageIcon buildCustomRocketIcon(RocketMaker maker) {
        if (maker == null) {
            return new ImageIcon(new BufferedImage(120, 160, BufferedImage.TYPE_INT_ARGB));
        }

        int width = 150;
        int height = 220;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double bodyLength = Math.max(0.10, maker.getBodyLength());
        double bodyDiameter = Math.max(0.015, maker.getBodyDiameter());
        double noseLength = maker.noseConeType != null ? maker.noseConeType.getLength() : 0.02;
        double engineLength = maker.engineType != null ? maker.engineType.length : 0.05;
        double finLen = maker.finType != null ? maker.finType.getH1() * maker.getFinSizeScale() * maker.getFinLengthScale() : 0.05;
        double finBase = maker.finType != null ? maker.finType.getBaseWidth() * maker.getFinSizeScale() : 0.04;

        double rocketLength = bodyLength + noseLength + engineLength + (finLen * 0.75);
        double rocketWidth = Math.max(bodyDiameter, finBase * 1.5);

        double scaleX = (width * 0.48) / Math.max(rocketWidth * 1500.0, 0.05);
        double scaleY = (height * 0.62) / Math.max(rocketLength * 1200.0, 0.30);
        double scale = Math.min(scaleX, scaleY);
        if (scale <= 0.0 || Double.isNaN(scale) || Double.isInfinite(scale)) {
            scale = 1.0;
        }

        int cx = width / 2;
        int bodyW = (int) Math.max(18, bodyDiameter * 1700.0 * scale);
        int bodyH = (int) Math.max(36, bodyLength * 950.0 * scale);
        int noseH = (int) Math.max(16, noseLength * 850.0 * scale);
        int engineH = (int) Math.max(10, engineLength * 450.0 * scale);
        int finLenPx = (int) Math.max(10, finLen * 1100.0 * scale);
        int finBasePx = (int) Math.max(7, finBase * 500.0 * scale);

        int noseTop = 24;
        int bodyY = noseTop + noseH;
        int engineY = bodyY + bodyH;

        g2.setColor(new Color(220, 240, 255));
        if (maker.noseConeType != null) {
            int noseBaseY = noseTop + noseH;
            int[] x = {cx, cx - bodyW / 2, cx + bodyW / 2};
            int[] y = {noseTop, noseBaseY, noseBaseY};
            g2.fillPolygon(x, y, 3);
        }

        g2.setColor(new Color(200, 225, 245));
        g2.fillRoundRect(cx - bodyW / 2, bodyY, bodyW, bodyH, 18, 18);

        g2.setColor(new Color(120, 130, 180));
        if (maker.finType != null) {
            int finY = bodyY + bodyH - 4;
            int leftX1 = cx - bodyW / 2;
            int leftX2 = cx - bodyW / 2 - finLenPx;
            int rightX1 = cx + bodyW / 2;
            int rightX2 = cx + bodyW / 2 + finLenPx;

            if (maker.finType == CustomConstants.finType.RECTANGLE) {
                int[] leftX = {leftX1, leftX1, leftX2, leftX2};
                int[] leftY = {finY, finY + finBasePx, finY + finBasePx, finY};
                g2.fillPolygon(leftX, leftY, 4);

                int[] rightX = {rightX1, rightX1, rightX2, rightX2};
                int[] rightY = {finY, finY + finBasePx, finY + finBasePx, finY};
                g2.fillPolygon(rightX, rightY, 4);
            } else {
                int[] leftX = {leftX1, leftX1, leftX2, leftX1 - finLenPx / 3};
                int[] leftY = {finY, finY + finBasePx, finY + finBasePx / 2, finY + finBasePx / 3};
                g2.fillPolygon(leftX, leftY, 4);

                int[] rightX = {rightX1, rightX1, rightX2, rightX1 + finLenPx / 3};
                int[] rightY = {finY, finY + finBasePx, finY + finBasePx / 2, finY + finBasePx / 3};
                g2.fillPolygon(rightX, rightY, 4);
            }
        }

        if (maker.engineType != null) {
            g2.setColor(new Color(255, 140, 60));
            g2.fillRoundRect(cx - 10, engineY, 20, engineH, 10, 10);
            g2.setColor(new Color(255, 180, 120));
            g2.fillRoundRect(cx - 7, engineY + 3, 14, Math.max(6, engineH - 6), 8, 8);
        }

        g2.setColor(new Color(40, 80, 140));
        g2.drawRoundRect(cx - bodyW / 2, bodyY, bodyW, bodyH, 18, 18);
        g2.drawPolygon(new int[]{cx, cx - bodyW / 2, cx + bodyW / 2}, new int[]{noseTop, noseTop + noseH, noseTop + noseH}, 3);

        g2.dispose();
        return new ImageIcon(image);
    }

    private static RoundButton styleRoundedButton(String text) {
        return styleRoundedButton(text, new Color(166, 225, 250), new Color(25, 25, 35), 18);
    }

    private static RoundButton styleRoundedButton(String text, Color background, Color foreground, int radius) {
        RoundButton button = new RoundButton(text, background, foreground, new Font("SansSerif", Font.BOLD, 14), radius);
        button.setMargin(new Insets(8, 18, 8, 18));
        return button;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(((AbstractButton) c).getBackground());
            g2.fill(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.setColor(new Color(255, 255, 255, 80));
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, width - 2, height - 2, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 12, 6, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 12;
            insets.right = 12;
            insets.top = 6;
            insets.bottom = 6;
            return insets;
        }
    }

    public static void main(String[] args) {
        try
        {
              UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        UIManager.put("Button.background", new Color(0, 0, 0, 0));
        UIManager.put("Button.foreground", new Color(25, 25, 35));
        UIManager.put("Panel.background", Color.decode("#00072d"));
        // updateUI();
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("rocket sim");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            // frame.setUndecorated(true);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.decode("#00072d"));
            frame.setContentPane(panel);
final int STAR_COUNT = 200;
final int[] starX = new int[STAR_COUNT];
final int[] starY = new int[STAR_COUNT];
final int[] starBaseSize = new int[STAR_COUNT];
final int[] starPhase = new int[STAR_COUNT];
for (int i = 0; i < STAR_COUNT; i++) {
    starX[i] = (int) (Math.random() * 2500);
    starY[i] = (int) (Math.random() * 1200);
    starBaseSize[i] =  (int) (Math.random() * 2);
    starPhase[i] = i * 17;
}

            JPanel homeScreen = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode("#00072d"));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    long time = System.currentTimeMillis();
                    for (int i = 0; i < STAR_COUNT; i++) {
                        int pulse = (int) (Math.sin(time / 220.0 + starPhase[i]) * 3);
                        int size = Math.max(1, starBaseSize[i] + pulse);
                        g2.setColor(Color.WHITE);
                        g2.fillOval(starX[i], starY[i], size, size);
                    }
                    g2.dispose();
                }
            };

            homeScreen.setLayout(new GridBagLayout());
            homeScreen.setBackground(new Color(0));
            homeScreen.setVisible(true);
            panel.add(homeScreen, BorderLayout.CENTER);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 0, 30, 0);

            JLabel titleImage = null;
            if (new java.io.File("/home/kasm-user/RocketProject/RocketSimProject/Assets/Title.png").exists()) {
                ImageIcon titleIcon = new ImageIcon("/home/kasm-user/RocketProject/RocketSimProject/Assets/Title.png");
                // Image scaledTitle = titleIcon.getImage().getScaledInstance(, 180, Image.SCALE_SMOOTH);
                titleImage = new JLabel(new ImageIcon(titleIcon.getImage().getScaledInstance(titleIcon.getIconWidth()/2, titleIcon.getIconHeight()/2, Image.SCALE_SMOOTH)));
                titleImage.setHorizontalAlignment(JLabel.CENTER);
                titleImage.setVisible(true);
                homeScreen.add(titleImage, gbc);
            }
            Timer timer = new Timer(40, e -> homeScreen.repaint());
            timer.start();

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);

            // JLabel titleGlow = new JLabel("Rocket Simulator");
            // titleGlow.setForeground(new Color(220, 210, 255, 120));
            // titleGlow.setFont(new Font("Serif", Font.BOLD, 62));
            // titleGlow.setBounds(130, 730, 780, 200);
            // titleGlow.setHorizontalAlignment(JLabel.CENTER);
            // homeScreen.add(titleGlow);

                // JLabel title = new JLabel("Rocket Simulator");
            // title.setForeground(new Color(245, 240, 255));
            // title.setFont(new Font("Serif", Font.BOLD, 60));
            // title.setBounds(120, 720, 780, 200);
            // title.setHorizontalAlignment(JLabel.CENTER);
            // homeScreen.add(title);
            ImageIcon starButtonDefault= new ImageIcon("C:\\Users\\awart\\Desktop\\rocket\\Assets\\starbutton (1).png");
            int width= starButtonDefault.getIconWidth()/5 ;
            int height= starButtonDefault.getIconHeight()/5;
            ImageIcon scaledStarButtonDefault= new ImageIcon(starButtonDefault.getImage().getScaledInstance( width, height,Image.SCALE_SMOOTH));
            ImageIcon starButtonHover= new ImageIcon("/home/kasm-user/RocketProject/RocketSimProject/Assets/starbutton.png");
            ImageIcon scaledStarButtonHover= new ImageIcon(starButtonHover.getImage().getScaledInstance( width, height,Image.SCALE_SMOOTH));
            
            JButton startButton = new JButton("<html><div style='text-shadow: -8px -8px 0 #000000, 8px -8px 0 #000000, -8px 8px 0 #000000, 8px 8px 0 #000000; font-weight: bold;'>Start</div></html>", scaledStarButtonDefault);

            startButton.setPreferredSize(new java.awt.Dimension(300, 300));
            startButton.setRolloverIcon(scaledStarButtonHover);
            startButton.setFont(new Font("Impact", Font.BOLD, 70));
            startButton.setForeground(new Color(245, 240, 255));
            startButton.setBorderPainted(false);
            startButton.setContentAreaFilled(false);
            startButton.setFocusPainted(false);
            startButton.setFocusable(false);
            startButton.setOpaque(false);
            startButton.setBackground(new Color(0, 0, 0, 0));
            startButton.setBorder(BorderFactory.createEmptyBorder());
            startButton.setMargin(new Insets(0, 0, 0, 0));
            startButton.setHorizontalTextPosition(SwingConstants.CENTER);
            startButton.setVerticalTextPosition(SwingConstants.CENTER);
            startButton.setBorder(null);
            homeScreen.add(startButton, gbc);
            homeScreen.repaint();
            startButton.addActionListener(e -> {
                panel.remove(homeScreen);

                JPanel mainMenuPanel = new JPanel(new BorderLayout(300, 0));
                mainMenuPanel.setBackground(Color.decode("#00072d"));
                panel.add(mainMenuPanel, BorderLayout.CENTER);

                JPanel optionsPanel = new JPanel();
                optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
                optionsPanel.setBackground(Color.decode("#00072d"));
                optionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                mainMenuPanel.add(optionsPanel, BorderLayout.WEST);

                JPanel previewPanel = new JPanel(new GridBagLayout());
                previewPanel.setBackground(Color.decode("#0b175c"));
                mainMenuPanel.add(previewPanel, BorderLayout.CENTER);

                JLabel simLabel = new JLabel("Rocket Simulator");
                simLabel.setFont(new Font("Serif", Font.BOLD, 44));
                simLabel.setForeground(new Color(245, 240, 255));
                simLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionsPanel.add(simLabel);
                optionsPanel.add(Box.createVerticalStrut(50));

                JLabel modelLabel = new JLabel("Model Rocket:");
                modelLabel.setForeground(new Color(245, 240, 255));
                modelLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
                modelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionsPanel.add(modelLabel);
                optionsPanel.add(Box.createVerticalStrut(50));

                JComboBox<RocketConstants.rocketModel> modelCombo = new JComboBox<>(RocketConstants.rocketModel.values());
                modelCombo.setSelectedItem(RocketConstants.rocketModel.Alpha3);
                modelCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
                modelCombo.setMaximumSize(new java.awt.Dimension(450, 100));
                optionsPanel.add(modelCombo);
                optionsPanel.add(Box.createVerticalStrut(50));

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
                buttonPanel.setBackground(new Color(0, 0, 0, 0));
                buttonPanel.setOpaque(false);
                buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                RoundButton launchButton = styleRoundedButton("Launch", new Color(244, 232, 255), new Color(45, 45, 60), 18);
                launchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                launchButton.setMaximumSize(new java.awt.Dimension(260, 58));
                launchButton.setFont(new Font("SansSerif", Font.BOLD, 22));
                launchButton.setBackground(null);
                launchButton.addActionListener(evt -> {
                    RocketConstants.rocketModel selectedModel = (RocketConstants.rocketModel) modelCombo.getSelectedItem();
                    if (selectedModel == null) {
                        return;
                    }

                    Rocket rocket = new Rocket();
                    rocket.setModel(selectedModel);
                    rocket.setLaunch(20);

                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    JFrame launchFrame = new JFrame("Launching: " + selectedModel.name());
                    launchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    launchFrame.setSize(screenSize);
                    launchFrame.setLocationRelativeTo(null);

                    JPanel launchPanel = new JPanel();
                    launchPanel.setLayout(null);
                    launchPanel.setBackground(new Color(42, 44, 52));
                    launchPanel.setPreferredSize(screenSize);
                    launchFrame.setContentPane(launchPanel);

                    int groundY = screenSize.height - 260;
                    int rocketBaseX = 140;
                    int rocketBaseY = groundY - 30;

                    JLabel launchLabel = new JLabel("Rocket Flight");
                    launchLabel.setForeground(new Color(245, 240, 255));
                    launchLabel.setFont(new Font("Serif", Font.BOLD, 36));
                    launchLabel.setBounds(300, 40, 400, 50);
                    launchPanel.add(launchLabel);

                    JLabel alt = new JLabel("0");
                    alt.setForeground(new Color(245, 240, 255));
                    alt.setBounds(50, 50, 200, 50);
                    launchPanel.add(alt);

                    ImageIcon icon = new ImageIcon(selectedModel.imgPath);
                    icon.setImage(icon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
                    RotatableLabel simRocket = new RotatableLabel(icon);
                    simRocket.setSize(100, 100);
                    simRocket.setLocation(rocketBaseX, rocketBaseY - simRocket.getHeight() + 24);

                    SimPanel pan = new SimPanel();
                    pan.setBounds(0, 0, screenSize.width, screenSize.height);
                    pan.setVisible(true);

                    JLabel simAngleLabel = new JLabel("Launch Angle: 20°");
                    simAngleLabel.setForeground(new Color(245, 240, 255));
                    simAngleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                    simAngleLabel.setBounds(screenSize.width - 300, 150, 200, 30);

                    JSlider simAngleSlider = new JSlider(0, 90, 20);
                    simAngleSlider.setBounds(screenSize.width - 300, 180, 230, 40);
                    simAngleSlider.setBackground(new Color(42, 44, 52));
                    simAngleSlider.setPaintTicks(true);
                    simAngleSlider.setPaintLabels(true);
                    simAngleSlider.setMajorTickSpacing(10);
                    simAngleSlider.setMinorTickSpacing(5);
                    simAngleSlider.addChangeListener(changeEvent -> simAngleLabel.setText("Launch Angle: " + simAngleSlider.getValue() + "°"));

                    JButton exitButton = createExitButton(screenSize.width - 180, 80, 140, 40);
                    exitButton.addActionListener(et -> launchFrame.dispose());

                    RoundButton relaunchButton = createReplayButton(screenSize.width - 180, 20, 140, 40);
                    relaunchButton.addActionListener(replayEvent -> {
                        rocket.setLaunch(simAngleSlider.getValue());
                        pan.clearTrajectory();
                        runRegularRocketSim(rocket, launchPanel, simRocket, alt, pan, exitButton, relaunchButton, simAngleSlider);
});                                                                                                             

                    launchPanel.add(pan);
                    launchPanel.add(simRocket);
                    launchPanel.add(launchLabel);
                    launchPanel.add(simAngleLabel);
                    launchPanel.add(simAngleSlider);
                    launchPanel.add(exitButton);
                    launchPanel.add(relaunchButton);

                    launchPanel.setComponentZOrder(pan, launchPanel.getComponentCount() - 1);

                    launchFrame.setVisible(true);
                    rocket.setLaunch(simAngleSlider.getValue());
                    runRegularRocketSim(rocket, launchPanel, simRocket, alt, pan, exitButton, relaunchButton, simAngleSlider);
                });
                buttonPanel.add(launchButton);
                buttonPanel.add(Box.createVerticalStrut(12));

                RoundButton customizeButton = styleRoundedButton("Customize Rocket", new Color(166, 225, 250), new Color(25, 25, 35), 18);
                customizeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                customizeButton.setMaximumSize(new java.awt.Dimension(260, 58));
                customizeButton.setFont(new Font("SansSerif", Font.BOLD, 20));
                customizeButton.addActionListener(evt -> openCustomizerFrame());
                buttonPanel.add(customizeButton);
                buttonPanel.add(Box.createVerticalStrut(12));

                RoundButton customLaunchButton = styleRoundedButton("Launch Custom Rocket", new Color(166, 225, 250), new Color(25, 25, 35), 18);
                customLaunchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                customLaunchButton.setMaximumSize(new java.awt.Dimension(260, 58));
                customLaunchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
                customLaunchButton.addActionListener(evt -> openCustomRocketLaunchMenu());
                buttonPanel.add(customLaunchButton);

                optionsPanel.add(buttonPanel);
                optionsPanel.add(Box.createVerticalGlue());
JLabel modelPreview = new JLabel();
modelPreview.setHorizontalAlignment(JLabel.CENTER);
modelPreview.setVerticalAlignment(JLabel.CENTER);
modelPreview.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

JPanel previewContainer = new JPanel(new BorderLayout(15, 0));
previewContainer.setOpaque(false);
previewContainer.add(modelPreview, BorderLayout.CENTER);

JPanel statsPanel = new JPanel();
statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
statsPanel.setBackground(new Color(120, 200, 255));
statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
statsPanel.setVisible(false);

JLabel statsTitle = new JLabel();
JLabel heightLabel = new JLabel();
JLabel widthLabel = new JLabel();
JLabel weightLabel = new JLabel();
JLabel cdLabel = new JLabel();

JLabel[] statLabels = {
    statsTitle, heightLabel, widthLabel, weightLabel, cdLabel
};

for (JLabel label : statLabels) {
    label.setForeground(Color.WHITE);
    statsPanel.add(label);
    statsPanel.add(Box.createVerticalStrut(10));
}

previewContainer.add(statsPanel, BorderLayout.EAST);
previewPanel.add(previewContainer);

                Runnable updateModelPreview = () -> {
                    RocketConstants.rocketModel selectedModel = (RocketConstants.rocketModel) modelCombo.getSelectedItem();
                    if (selectedModel == null) {
                        return;
                    }
                    ImageIcon iconPreview = new ImageIcon(selectedModel.imgPath);
                    Image scaled = iconPreview.getImage().getScaledInstance(550, 550, Image.SCALE_SMOOTH);
                    modelPreview.setIcon(new ImageIcon(scaled));
statsTitle.setText(selectedModel.name());
heightLabel.setText("Height: " + selectedModel.height);
widthLabel.setText("Width: " + selectedModel.width);
weightLabel.setText("Weight: " + selectedModel.weight);
cdLabel.setText("Drag Coefficient: " + selectedModel.cd);
                };
                updateModelPreview.run();
                modelCombo.addActionListener(ea -> updateModelPreview.run());
java.awt.event.MouseAdapter hoverListener = new java.awt.event.MouseAdapter() {
    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        statsPanel.setVisible(true);

        modelPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(120, 200, 255), 10, true
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        previewContainer.revalidate();
        previewContainer.repaint();
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        java.awt.Point mouse = e.getLocationOnScreen();
        javax.swing.SwingUtilities.convertPointFromScreen(
            mouse, previewContainer
        );

        if (!previewContainer.contains(mouse)) {
            statsPanel.setVisible(false);
            modelPreview.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            );

            previewContainer.revalidate();
            previewContainer.repaint();
        }
    }
};

modelPreview.addMouseListener(hoverListener);
statsPanel.addMouseListener(hoverListener);

                panel.revalidate();
                panel.repaint();
            });
            frame.setVisible(true);
        });
    }
    public void bounceTitle(JLabel title)
    {
        

    }

    private static void openCustomizerFrame() {
        RocketMaker rocketMaker = new RocketMaker();
        rocketMaker.chooseNoseConeType(CustomConstants.noseCone.Cone);
        rocketMaker.chooseBodyType(CustomConstants.body.Standard);
        rocketMaker.chooseFinType(CustomConstants.finType.TRIANGLE);
        rocketMaker.chooseEngine(CustomConstants.rocketEngine.A8_3);

        JFrame customizerFrame = new JFrame("Customize Rocket");
        customizerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customizerFrame.setSize(1000, 1000);
        customizerFrame.setLocationRelativeTo(null);

        JPanel customizerPanel = new JPanel(new BorderLayout());
        customizerPanel.setBackground(new Color(42, 44, 52));
        customizerFrame.setContentPane(customizerPanel);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(new Color(42, 44, 52));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        controlsPanel.setPreferredSize(new java.awt.Dimension(250, 1000));
        customizerPanel.add(controlsPanel, BorderLayout.WEST);

        CustomizationPanel customizationPanel = new CustomizationPanel(rocketMaker);
        customizationPanel.setPreferredSize(new java.awt.Dimension(720, 860));
        customizerPanel.add(customizationPanel, BorderLayout.CENTER);

        JLabel customizerTitle = new JLabel("Customize Rocket");
        customizerTitle.setForeground(new Color(245, 240, 255));
        customizerTitle.setFont(new Font("Serif", Font.BOLD, 44));
        customizerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(customizerTitle);
        controlsPanel.add(Box.createVerticalStrut(14));

        JLabel noseConeLabel = new JLabel("Nose Cone:");
        noseConeLabel.setForeground(new Color(245, 240, 255));
        noseConeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(noseConeLabel);

        JComboBox<CustomConstants.noseCone> noseConeCombo = new JComboBox<>(CustomConstants.noseCone.values());
        noseConeCombo.setSelectedItem(CustomConstants.noseCone.Cone);
        noseConeCombo.setMaximumSize(new java.awt.Dimension(180, 30));
        noseConeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(noseConeCombo);
        controlsPanel.add(Box.createVerticalStrut(14));

        JLabel bodyLengthLabel = new JLabel("Body Length: 0.400 m");
        bodyLengthLabel.setForeground(new Color(245, 240, 255));
        bodyLengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(bodyLengthLabel);

        JSlider bodyLengthSlider = new JSlider(10, 50, 40);
        bodyLengthSlider.setMaximumSize(new java.awt.Dimension(180, 40));
        bodyLengthSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(bodyLengthSlider);
        controlsPanel.add(Box.createVerticalStrut(14));

        JLabel finTypeLabel = new JLabel("Fin Type:");
        finTypeLabel.setForeground(new Color(245, 240, 255));
        finTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finTypeLabel);

        JComboBox<CustomConstants.finType> finTypeCombo = new JComboBox<>(CustomConstants.finType.values());
        finTypeCombo.setSelectedItem(CustomConstants.finType.TRIANGLE);
        finTypeCombo.setMaximumSize(new java.awt.Dimension(180, 30));
        finTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finTypeCombo);
        controlsPanel.add(Box.createVerticalStrut(14));

        JLabel engineLabel = new JLabel("Engine Type:");
        engineLabel.setForeground(new Color(245, 240, 255));
        engineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(engineLabel);

        JComboBox<CustomConstants.rocketEngine> engineCombo = new JComboBox<>(CustomConstants.rocketEngine.values());
        engineCombo.setSelectedItem(CustomConstants.rocketEngine.A8_3);
        engineCombo.setMaximumSize(new java.awt.Dimension(180, 30));
        engineCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(engineCombo);
        controlsPanel.add(Box.createVerticalStrut(14));

        JLabel bodyDensityLabel = new JLabel("Body Density: 1.00x");
        bodyDensityLabel.setForeground(new Color(245, 240, 255));
        bodyDensityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(bodyDensityLabel);

        JSlider bodyDensitySlider = new JSlider(10, 300, 100);
        bodyDensitySlider.setMaximumSize(new java.awt.Dimension(180, 40));
        bodyDensitySlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(bodyDensitySlider);
        controlsPanel.add(Box.createVerticalStrut(10));

        JLabel noseDensityLabel = new JLabel("Nose Density: 1.00x");
        noseDensityLabel.setForeground(new Color(245, 240, 255));
        noseDensityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(noseDensityLabel);

        JSlider noseDensitySlider = new JSlider(10, 300, 100);
        noseDensitySlider.setMaximumSize(new java.awt.Dimension(180, 40));
        noseDensitySlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(noseDensitySlider);
        controlsPanel.add(Box.createVerticalStrut(10));

        JLabel finSizeLabel = new JLabel("Fin Size: 1.00x");
        finSizeLabel.setForeground(new Color(245, 240, 255));
        finSizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finSizeLabel);

        JSlider finSizeSlider = new JSlider(10, 300, 100);
        finSizeSlider.setMaximumSize(new java.awt.Dimension(180, 40));
        finSizeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finSizeSlider);
        controlsPanel.add(Box.createVerticalStrut(10));

        JLabel finLengthLabel = new JLabel("Fin Length: 1.00x");
        finLengthLabel.setForeground(new Color(245, 240, 255));
        finLengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finLengthLabel);

        JSlider finLengthSlider = new JSlider(10, 300, 100);
        finLengthSlider.setMaximumSize(new java.awt.Dimension(180, 40));
        finLengthSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(finLengthSlider);
        controlsPanel.add(Box.createVerticalStrut(16));

        JLabel cgLabel = new JLabel("Center of Gravity: ");
        JLabel cpLabel = new JLabel("Center of Pressure: ");
        cgLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        cpLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        cgLabel.setForeground(new Color(245, 240, 255));
        cpLabel.setForeground(new Color(245, 240, 255));
        cgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(cgLabel);
        controlsPanel.add(cpLabel);
        controlsPanel.add(Box.createVerticalStrut(8));

        JLabel stabilityLabel = new JLabel("Stability: STABLE");
        stabilityLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        stabilityLabel.setForeground(new Color(100, 200, 100));
        stabilityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsPanel.add(stabilityLabel);
        controlsPanel.add(Box.createVerticalStrut(18));

        JButton calcCGButton = styleRoundedButton("Calculate Values", new Color(166, 225, 250), new Color(25, 25, 35), 18);
        calcCGButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcCGButton.setMaximumSize(new java.awt.Dimension(180, 36));
        controlsPanel.add(calcCGButton);
        controlsPanel.add(Box.createVerticalStrut(12));

        JButton saveRocketButton = styleRoundedButton("Save Rocket", new Color(200, 230, 255), new Color(25, 25, 35), 18);
        saveRocketButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveRocketButton.setMaximumSize(new java.awt.Dimension(180, 36));
        controlsPanel.add(saveRocketButton);

        controlsPanel.add(Box.createVerticalGlue());

        Runnable refreshInfo = () -> {
            updateCGLabel(rocketMaker, cgLabel, cpLabel, stabilityLabel);
            customizationPanel.update();
        };

        noseConeCombo.addActionListener(e -> {
            rocketMaker.chooseNoseConeType((CustomConstants.noseCone) noseConeCombo.getSelectedItem());
            refreshInfo.run();
        });

        bodyLengthSlider.addChangeListener(e -> {
            double bodyLength = bodyLengthSlider.getValue() / 100.0;
            rocketMaker.setBodyLength(bodyLength);
            bodyLengthLabel.setText(String.format("Body Length: %.3f m", bodyLength));
            refreshInfo.run();
        });

        finTypeCombo.addActionListener(e -> {
            rocketMaker.chooseFinType((CustomConstants.finType) finTypeCombo.getSelectedItem());
            refreshInfo.run();
        });

        engineCombo.addActionListener(e -> {
            rocketMaker.chooseEngine((CustomConstants.rocketEngine) engineCombo.getSelectedItem());
            refreshInfo.run();
        });

        bodyDensitySlider.addChangeListener(e -> {
            double density = bodyDensitySlider.getValue() / 100.0;
            rocketMaker.setBodyTubeDensity(density);
            bodyDensityLabel.setText(String.format("Body Density: %.2fx", density));
            refreshInfo.run();
        });

        noseDensitySlider.addChangeListener(e -> {
            double density = noseDensitySlider.getValue() / 100.0;
            rocketMaker.setNoseConeDensity(density);
            noseDensityLabel.setText(String.format("Nose Density: %.2fx", density));
            refreshInfo.run();
        });

        finSizeSlider.addChangeListener(e -> {
            double finScale = finSizeSlider.getValue() / 100.0;
            rocketMaker.setFinSizeScale(finScale);
            finSizeLabel.setText(String.format("Fin Size: %.2fx", finScale));
            refreshInfo.run();
        });

        finLengthSlider.addChangeListener(e -> {
            double finLength = finLengthSlider.getValue() / 100.0;
            rocketMaker.setFinLengthScale(finLength);
            finLengthLabel.setText(String.format("Fin Length: %.2fx", finLength));
            refreshInfo.run();
        });

        calcCGButton.addActionListener(e -> refreshInfo.run());

        saveRocketButton.addActionListener(e -> {
            String rocketName = JOptionPane.showInputDialog(customizerFrame, "Name this rocket:", "Save Custom Rocket", JOptionPane.PLAIN_MESSAGE);
            if (rocketName == null || rocketName.trim().isEmpty()) {
                return;
            }
            try {
                CustomRocketStorage.saveRocket(rocketMaker, rocketName);
                JOptionPane.showMessageDialog(customizerFrame, "Saved custom rocket: " + rocketName, "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(customizerFrame, "Could not save rocket: " + ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshInfo.run();
        customizerFrame.setVisible(true);
    }

    private static void updateCGLabel(RocketMaker rocketMaker, JLabel cgLabel, JLabel cpLabel, JLabel stabilityLabel) {
        if (rocketMaker == null || cgLabel == null || cpLabel == null) {
            return;
        }

        double cg = rocketMaker.calcOverallCG();
        double cp = rocketMaker.calcOverallCP();
        cgLabel.setText(String.format("Overall CG: %.4f m from nose", cg));
        cpLabel.setText(String.format("CP estimate: %.4f m from nose", cp));

        if (stabilityLabel != null) {
            double stabilityMargin = rocketMaker.calcStabilityMargin();
            if (stabilityMargin > 0.005) {
                stabilityLabel.setText(String.format("Stability: STABLE (margin: %.4f m)", stabilityMargin));
                stabilityLabel.setForeground(new Color(100, 200, 100));
            } else {
                stabilityLabel.setText(String.format("Stability: UNSTABLE (margin: %.4f m)", stabilityMargin));
                stabilityLabel.setForeground(new Color(255, 100, 100));
            }
        }
    }

    private static void openCustomRocketLaunchMenu() {
        List<String> customRocketNames = CustomRocketStorage.listRocketNames();
        if (customRocketNames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No saved custom rockets yet.", "Custom Rockets", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> rocketCombo = new JComboBox<>(customRocketNames.toArray(new String[0]));
        int result = JOptionPane.showConfirmDialog(null, rocketCombo, "Choose a custom rocket", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedName = (String) rocketCombo.getSelectedItem();
        if (selectedName == null || selectedName.trim().isEmpty()) {
            return;
        }

        try {
            RocketMaker customRocketMaker = CustomRocketStorage.loadRocket(selectedName);
            CustomRocket customRocket = new CustomRocket(customRocketMaker);
            customRocket.setFuelMass(0.02);
            customRocket.setFuelBurnTime(1.2);
            customRocket.setInitialVelocity(0.0);
            customRocket.setTotalImpulse(8.0);
            customRocket.setRocketMass(customRocketMaker.calcTotalMass());
            customRocket.setTime(20.0);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            JFrame launchFrame = new JFrame("Launching: " + selectedName);
            launchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            launchFrame.setSize(screenSize);
            launchFrame.setLocationRelativeTo(null);

            JPanel launchPanel = new JPanel();
            launchPanel.setLayout(null);
            launchPanel.setBackground(new Color(42, 44, 52));
            launchPanel.setPreferredSize(screenSize);
            launchFrame.setContentPane(launchPanel);

            int groundY = Math.max(220, screenSize.height - 220);
            int rocketBaseX = 120;
            int rocketBaseY = groundY;

            JLabel launchLabel = new JLabel("Custom Rocket Flight");
            launchLabel.setForeground(new Color(245, 240, 255));
            launchLabel.setFont(new Font("Serif", Font.BOLD, 36));
            launchLabel.setBounds(280, 40, 440, 50);
            launchPanel.add(launchLabel);

            JLabel alt = new JLabel("0");
            alt.setForeground(new Color(245, 240, 255));
            alt.setBounds(50, 50, 200, 50);
            launchPanel.add(alt);

            ImageIcon icon = buildCustomRocketIcon(customRocketMaker);
            int targetW = 68;
            int targetH = 92;
            icon.setImage(icon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH));
            RotatableLabel simRocket = new RotatableLabel(icon);
            simRocket.setSize(targetW, targetH);
            simRocket.setLocation(rocketBaseX, rocketBaseY - simRocket.getHeight() + 24);

            SimPanel pan = new SimPanel();
            pan.setBounds(0, 0, screenSize.width, screenSize.height);
            pan.setVisible(true);

            JButton exitButton = createExitButton(screenSize.width - 180, 80, 140, 40);
            exitButton.addActionListener(et -> launchFrame.dispose());

            JLabel customAngleLabel = new JLabel("Launch Angle: 20°");
            customAngleLabel.setForeground(new Color(245, 240, 255));
            customAngleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            customAngleLabel.setBounds(screenSize.width - 320, 150, 220, 30);

            JSlider customAngleSlider = createLaunchAngleSlider(screenSize.width - 320, 180, 220, 20);
            bindLaunchAngleLabel(customAngleLabel, customAngleSlider);

            RoundButton relaunchButton = createReplayButton(screenSize.width - 180, 20, 140, 40);
            relaunchButton.setOpaque(true);
            relaunchButton.setBorderPainted(false);
            relaunchButton.addActionListener(evt -> {
                int angle = customAngleSlider.getValue();
                customRocket.setTime(20.0);
                pan.clearTrajectory();
                runCustomRocketSim(customRocket, launchPanel, simRocket, alt, pan, exitButton, angle, relaunchButton);
            });

            launchPanel.add(pan);
            launchPanel.add(simRocket);
            launchPanel.add(launchLabel);
            launchPanel.add(alt);
            launchPanel.add(exitButton);
            launchPanel.add(customAngleLabel);
            launchPanel.add(customAngleSlider);
            launchPanel.add(relaunchButton);

            launchPanel.setComponentZOrder(pan, launchPanel.getComponentCount() - 1);

            launchFrame.setVisible(true);
            runCustomRocketSim(customRocket, launchPanel, simRocket, alt, pan, exitButton, customAngleSlider.getValue(), relaunchButton);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to load rocket: " + ex.getMessage(), "Load failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void runRegularRocketSim(Rocket rocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton, RoundButton replayButton, JSlider simAngleSlider) {
        final double[] currentStats = new double[5];
        final double[] info = new double[6];
        final double[] scales = new double[2];

        pan.clearTrajectory();
        if (replayButton != null) {
            replayButton.setVisible(true);
        }

        SwingWorker<double[], Void> worker = new SwingWorker<>() {
            @Override
            protected double[] doInBackground() {
                return rocket.launch(panel);
            }

            @Override
            protected void done() {
                try {
                    double[] result = get();
                    if (result == null || result.length < 6) {
                        return;
                    }
                    info[0] = result[0];
                    info[1] = result[1];
                    info[2] = result[2];
                    info[3] = result[3];
                    info[4] = result[4];
                    info[5] = result[5];

                    double xMax = info[2];
                    double yMax = info[3];
                    double[] graphScale = scaleGraph(xMax, yMax);
                    scales[0] = graphScale[0];
                    scales[1] = graphScale[1];

                    pan.xScale = scales[0];
                    pan.yScl = scales[1];
                    pan.xMax = xMax;
                    pan.yMax = yMax;

                    panel.repaint();
                    startRegularAnimation(
                            scanPointsFile(panel, simRocket, pan, alt, currentStats, scales[0], scales[1]),
                            panel,
                            simRocket,
                            pan,
                            alt,
                            currentStats
                    );
                    if (replayButton != null) {
                        replayButton.setVisible(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private static void runRegularRocketSim(Rocket rocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton) {
        runRegularRocketSim(rocket, panel, simRocket, alt, pan, exitButton, null, null);
    }

    private static Timer startRegularAnimation(Scanner scan, JPanel panel, RotatableLabel simRocket, SimPanel pan, JLabel alt, double[] currentStats) {
        Timer timer = new Timer(18, null);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "togglePauseRegular");
        panel.getActionMap().put("togglePauseRegular", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        });

        timer.addActionListener((ActionEvent event) -> {
            if (scan.hasNextLine()) {
                String coords = scan.nextLine();
                String[] parts = coords.split(",");

                if (parts.length >= 5) {
                    double rawX = Double.parseDouble(parts[0].trim());
                    double rawY = Double.parseDouble(parts[1].trim());
                    double heading = Double.parseDouble(parts[2].trim());
                    double vectorMagX = Double.parseDouble(parts[3].trim());
                    double vectorMagY = Double.parseDouble(parts[4].trim());

                    if (rawX < 0) rawX = 0;
                    if (rawY < 0) rawY = 0;

                    currentStats[0] = rawX;
                    currentStats[1] = rawY;
                    currentStats[2] = heading;
                    currentStats[3] = vectorMagX;
                    currentStats[4] = vectorMagY;

                    double worldMaxX = Math.max(150.0, pan.xMax);
                    double worldMaxY = Math.max(150.0, pan.yMax);
                    int panelWidth = panel.getWidth() > 0 ? panel.getWidth() : 1200;
                    int panelHeight = panel.getHeight() > 0 ? panel.getHeight() : 800;
                    int groundY = panelHeight - 175;
                    int marginX = 120;
                    int marginY = 90;

                    double xScale = (panelWidth - marginX * 2) / worldMaxX;
                    double yScale = (groundY - marginY) / worldMaxY;
                    int xPosition = (int) (marginX + (rawX * xScale));
                    int yPosition = (int) (groundY - (rawY * yScale));
                    if (yPosition > groundY) yPosition = groundY;

                    pan.xScale = xScale;
                    pan.yScl = yScale;
                    pan.update((int) vectorMagX, 20, 0, xPosition, yPosition, (int) vectorMagY, 20, 90);
                    pan.repaint();
                    simRocket.setLocation(xPosition - simRocket.getWidth() / 2, yPosition - simRocket.getHeight() + 20);
                    simRocket.setAngle(90 - heading);
                    alt.setText(String.format("Altitude: %.2f m", rawY));
                    panel.repaint();
                }

                if (!scan.hasNextLine()) {
                    timer.stop();
                    scan.close();
                    System.out.println("Regular rocket animation complete.");
                }
            }
        });

        timer.start();
        return timer;
    }

    private static Scanner scanPointsFile(JPanel panel, RotatableLabel simRocket, SimPanel pan, JLabel alt, double[] currentStats, double scaleX, double scaleY) {
        try {
            return new Scanner(Path.of("points.txt"));
        } catch (IOException e) {
            System.err.println("Error reading points.txt: " + e.getMessage());
            return new Scanner("");
        }
    }

    public static double[] scaleGraph(double maxX, double maxY) {
        double targetViewSize = 800.0;
        double workingMaxX = Math.max(300.0, maxX);
        double workingMaxY = Math.max(300.0, maxY);
        double scaleX = targetViewSize / workingMaxX;
        double scaleY = targetViewSize / workingMaxY;
        return new double[]{scaleX, scaleY};
    }

    private static void runCustomRocketSim(CustomRocket customRocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton, double launchAngle) {
        runCustomRocketSim(customRocket, panel, simRocket, alt, pan, exitButton, launchAngle, null);
    }

    private static void runCustomRocketSim(CustomRocket customRocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton, double launchAngle, RoundButton replayButton) {
        final double[] currentStats = new double[5];
        final double[] info = new double[6];
        final double[] scales = new double[2];

        pan.clearTrajectory();
        if (replayButton != null) {
            replayButton.setVisible(true);
        }

        SwingWorker<double[], Void> worker = new SwingWorker<>() {
            @Override
            protected double[] doInBackground() {
                return customRocket.launch(launchAngle);
            }

            @Override
            protected void done() {
                try {
                    double[] result = get();
                    if (result == null || result.length < 6) {
                        return;
                    }

                    info[0] = result[0];
                    info[1] = result[1];
                    info[2] = result[2];
                    info[3] = result[3];
                    info[4] = result[4];
                    info[5] = result[5];

                    double xMax = info[2];
                    double yMax = info[3];
                    double[] graphScale = scaleGraph(xMax, yMax);
                    scales[0] = graphScale[0];
                    scales[1] = graphScale[1];

                    pan.xScale = scales[0];
                    pan.yScl = scales[1];
                    pan.xMax = xMax;
                    pan.yMax = yMax;

                    panel.repaint();
                    startCustomAnimation(
                            scanPointsFile(panel, simRocket, pan, alt, currentStats, scales[0], scales[1]),
                            panel,
                            simRocket,
                            pan,
                            alt,
                            currentStats
                    );
                    if (replayButton != null) {
                        replayButton.setVisible(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private static void runCustomRocketSim(CustomRocket customRocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton, double launchAngle, RoundButton replayButton, boolean immediateStart) {
        runCustomRocketSim(customRocket, panel, simRocket, alt, pan, exitButton, launchAngle, replayButton);
    }

    private static Timer startCustomAnimation(Scanner scan, JPanel panel, RotatableLabel simRocket, SimPanel pan, JLabel alt, double[] currentStats) {
        Timer timer = new Timer(18, null);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "togglePauseCustom");
        panel.getActionMap().put("togglePauseCustom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        });

        timer.addActionListener((ActionEvent event) -> {
            if (scan.hasNextLine()) {
                String coords = scan.nextLine();
                String[] parts = coords.split(",");

                if (parts.length >= 5) {
                    double rawX = Double.parseDouble(parts[0].trim());
                    double rawY = Double.parseDouble(parts[1].trim());
                    double heading = Double.parseDouble(parts[2].trim());
                    double vectorMagX = Double.parseDouble(parts[3].trim());
                    double vectorMagY = Double.parseDouble(parts[4].trim());

                    if (rawX < 0) rawX = 0;
                    if (rawY < 0) rawY = 0;

                    currentStats[0] = rawX;
                    currentStats[1] = rawY;
                    currentStats[2] = heading;
                    currentStats[3] = vectorMagX;
                    currentStats[4] = vectorMagY;

                    double worldMaxX = Math.max(150.0, pan.xMax);
                    double worldMaxY = Math.max(150.0, pan.yMax);
                    int panelWidth = panel.getWidth() > 0 ? panel.getWidth() : 1200;
                    int panelHeight = panel.getHeight() > 0 ? panel.getHeight() : 800;
                    int groundY = panelHeight - 175;
                    int marginX = 120;
                    int marginY = 90;

                    double xScale = (panelWidth - marginX * 2) / worldMaxX;
                    double yScale = (groundY - marginY) / worldMaxY;
                    int xPosition = (int) (marginX + (rawX * xScale));
                    int yPosition = (int) (groundY - (rawY * yScale));
                    if (yPosition > groundY) yPosition = groundY;

                    pan.xScale = xScale;
                    pan.yScl = yScale;
                    pan.update((int) vectorMagX, 20, 0, xPosition, yPosition, (int) vectorMagY, 20, 90);
                    pan.repaint();
                    simRocket.setLocation(xPosition - simRocket.getWidth() / 2, yPosition - simRocket.getHeight() + 20);
                    simRocket.setAngle(90 - heading);
                    alt.setText(String.format("Altitude: %.2f m", rawY));
                    panel.repaint();
                }

                if (!scan.hasNextLine()) {
                    timer.stop();
                    scan.close();
                    System.out.println("Custom rocket animation complete.");
                }
            }
        });

        timer.start();
        return timer;
    }

    private static RoundButton createReplayButton(int x, int y, int width, int height) {
        RoundButton button = styleRoundedButton("Re-Launch", new Color(166, 225, 250), new Color(25, 25, 35), 18);
        button.setBounds(x, y, width, height);
        button.setVisible(true);
        return button;
    }

    private static JButton createExitButton(int x, int y, int width, int height) {
        JButton button = styleRoundedButton("Exit", new Color(244, 232, 255), new Color(45, 45, 60), 18);
        button.setBounds(x, y, width, height);
        return button;
    }

    private static JSlider createLaunchAngleSlider(int x, int y, int width, int initialValue) {
        JSlider slider = new JSlider(0, 90, initialValue);
        slider.setBounds(x, y, width, 40);
        slider.setBackground(new Color(42, 44, 52));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        return slider;
    }

    private static void orderSimulationOverlay(JPanel panel, JComponent background, JComponent... foreground) {
        int order = 0;
        if (background != null) {
            panel.setComponentZOrder(background, order++);
        }
        for (JComponent component : foreground) {
            if (component != null) {
                panel.setComponentZOrder(component, order++);
            }
        }
    }

    private static void bindLaunchAngleLabel(JLabel angleLabel, JSlider angleSlider) {
        angleSlider.addChangeListener(changeEvent -> {
            angleLabel.setText("Launch Angle: " + angleSlider.getValue() + "°");
        });
    }
}
