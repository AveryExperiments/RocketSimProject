import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import javax.swing.AbstractAction;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main {

    public static void main(String[] args) {
        try
        {
              UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        UIManager.put("Button.background", Color.decode("#A6E1FA"));
        // updateUI();
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("rocket sim");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 1000);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(new Color(42, 44, 52));
            frame.setContentPane(panel);

            JPanel homeScreen = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode("#00072d"));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            homeScreen.setLayout(null);
            // homeScreen.setBackground(new Color(0, 0, 0, 0));
            homeScreen.setBounds(0, 0, 1000, 1000);
            panel.add(homeScreen);

            JLabel titleImage = null;
            if (new java.io.File("Assets/Title.png").exists()) {
                ImageIcon titleIcon = new ImageIcon("Assets/Title.png");
                Image scaledTitle = titleIcon.getImage().getScaledInstance(820, 180, Image.SCALE_SMOOTH);
                titleImage = new JLabel(new ImageIcon(scaledTitle));
                titleImage.setBounds(90, 120, 820, 180);
                homeScreen.add(titleImage);
            }

            JLabel titleGlow = new JLabel("Rocket Simulator");
            titleGlow.setForeground(new Color(220, 210, 255, 120));
            titleGlow.setFont(new Font("Serif", Font.BOLD, 62));
            titleGlow.setBounds(130, 730, 780, 200);
            titleGlow.setHorizontalAlignment(JLabel.CENTER);
            homeScreen.add(titleGlow);

            JLabel title = new JLabel("Rocket Simulator");
            title.setForeground(new Color(245, 240, 255));
            title.setFont(new Font("Serif", Font.BOLD, 60));
            title.setBounds(120, 720, 780, 200);
            title.setHorizontalAlignment(JLabel.CENTER);
            homeScreen.add(title);
            
            JButton startButton = new JButton("Start Simulator");
            startButton.setBounds(390, 500, 220, 60);
// 
            startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
            startButton.setFocusPainted(false);
            startButton.setBorderPainted(false);
            startButton.setOpaque(true);
            homeScreen.add(startButton);

            startButton.addActionListener(e -> {
                panel.remove(homeScreen);

                JComboBox<RocketConstants.rocketModel> modelCombo = new JComboBox<>(RocketConstants.rocketModel.values());
                modelCombo.setSelectedItem(RocketConstants.rocketModel.Alpha3);

                JSlider angle = new JSlider(0, 90, 20);
                angle.setValue(20);
                angle.setPaintTicks(true);
                angle.setPaintLabels(true);
                angle.setMajorTickSpacing(10);
                angle.setMinorTickSpacing(5);

                JButton launchButton = new JButton("Launch");
                launchButton.setBounds(100, 400, 100, 50);
                launchButton.addActionListener(evt -> {
                    RocketConstants.rocketModel selectedModel = (RocketConstants.rocketModel) modelCombo.getSelectedItem();
                    if (selectedModel == null) {
                        return;
                    }

                    Rocket rocket = new Rocket();
                    rocket.setModel(selectedModel);
                    rocket.setLaunch(angle.getValue());

                    JFrame launchFrame = new JFrame("Launching: " + selectedModel.name());
                    launchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    launchFrame.setSize(1000, 1000);
                    launchFrame.setLocationRelativeTo(null);

                    JPanel launchPanel = new JPanel();
                    launchPanel.setLayout(null);
                    launchPanel.setBackground(new Color(42, 44, 52));
                    launchFrame.setContentPane(launchPanel);

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
                    simRocket.setLocation(100, 850 - simRocket.getHeight() + 40);
                    launchPanel.add(simRocket);

                    JButton exitButton = new JButton("Exit");
                    exitButton.setBounds(850, 20, 120, 40);
                    exitButton.addActionListener(et -> launchFrame.dispose());
                    launchPanel.add(exitButton);

                    SimPanel pan = new SimPanel();
                    pan.setBounds(0, 0, 1000, 1000);
                    pan.setVisible(true);
                    launchPanel.add(pan);
                    launchPanel.setComponentZOrder(pan, launchPanel.getComponentCount() - 1);

                    launchFrame.setVisible(true);
                    runRegularRocketSim(rocket, launchPanel, simRocket, alt, pan, exitButton);
                });
                panel.add(launchButton);

                JButton customizeButton = new JButton("Customize Rocket");
                customizeButton.setBounds(100, 600, 150, 50);
                customizeButton.addActionListener(evt -> openCustomizerFrame());
                panel.add(customizeButton);

                JButton customLaunchButton = new JButton("Launch Custom Rocket");
                customLaunchButton.setBounds(100, 675, 190, 50);
                customLaunchButton.addActionListener(evt -> openCustomRocketLaunchMenu());
                panel.add(customLaunchButton);

                JLabel simLabel = new JLabel("Rocket Simulator");
                simLabel.setFont(new Font("Serif", Font.BOLD, 44));
                simLabel.setForeground(new Color(245, 240, 255));
                simLabel.setBounds(600, 75, 400, 400);
                panel.add(simLabel);

                JLabel modelLabel = new JLabel("Model Rocket:");
                modelLabel.setForeground(new Color(245, 240, 255));
                modelLabel.setBounds(600, 420, 180, 30);
                panel.add(modelLabel);

                modelCombo.setBounds(600, 450, 200, 30);
                panel.add(modelCombo);

                JLabel modelPreview = new JLabel();
                modelPreview.setBounds(830, 420, 150, 150);
                modelPreview.setHorizontalAlignment(JLabel.CENTER);
                modelPreview.setVerticalAlignment(JLabel.CENTER);
                panel.add(modelPreview);

                Runnable updateModelPreview = () -> {
                    RocketConstants.rocketModel selectedModel = (RocketConstants.rocketModel) modelCombo.getSelectedItem();
                    if (selectedModel == null) {
                        return;
                    }
                    ImageIcon iconPreview = new ImageIcon(selectedModel.imgPath);
                    Image scaled = iconPreview.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    modelPreview.setIcon(new ImageIcon(scaled));
                };
                updateModelPreview.run();
                modelCombo.addActionListener(ea -> updateModelPreview.run());

                angle.setBounds(600, 540, 220, 40);
                panel.add(angle);

                JLabel angleLabel = new JLabel("Launch Angle: " + angle.getValue());
                angleLabel.setForeground(new Color(245, 240, 255));
                angleLabel.setBounds(600, 500, 200, 30);
                panel.add(angleLabel);

                angle.addChangeListener(changeEvent -> angleLabel.setText("Launch Angle: " + angle.getValue()));

                panel.revalidate();
                panel.repaint();
            });

            frame.setVisible(true);
        });
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

        JPanel customizerPanel = new JPanel();
        customizerPanel.setLayout(null);
        customizerPanel.setBackground(new Color(42, 44, 52));
        customizerFrame.setContentPane(customizerPanel);

        CustomizationPanel customizationPanel = new CustomizationPanel(rocketMaker);
        customizationPanel.setBounds(230, 20, 720, 860);
        customizerPanel.add(customizationPanel);

        JLabel customizerTitle = new JLabel("Customize Rocket");
        customizerTitle.setForeground(new Color(245, 240, 255));
        customizerTitle.setFont(new Font("Serif", Font.BOLD, 44));
        customizerTitle.setBounds(230, 20, 320, 60);
        customizerPanel.add(customizerTitle);

        JLabel noseConeLabel = new JLabel("Nose Cone:");
        noseConeLabel.setForeground(new Color(245, 240, 255));
        noseConeLabel.setBounds(20, 70, 100, 25);
        customizerPanel.add(noseConeLabel);

        JComboBox<CustomConstants.noseCone> noseConeCombo = new JComboBox<>(CustomConstants.noseCone.values());
        noseConeCombo.setSelectedItem(CustomConstants.noseCone.Cone);
        noseConeCombo.setBounds(20, 100, 120, 25);
        customizerPanel.add(noseConeCombo);

        JLabel bodyLengthLabel = new JLabel("Body Length: 0.400 m");
        bodyLengthLabel.setForeground(new Color(245, 240, 255));
        bodyLengthLabel.setBounds(20, 150, 180, 25);
        customizerPanel.add(bodyLengthLabel);

        JSlider bodyLengthSlider = new JSlider(10, 50, 40);
        bodyLengthSlider.setBounds(20, 180, 120, 40);
        customizerPanel.add(bodyLengthSlider);

        JLabel finTypeLabel = new JLabel("Fin Type:");
        finTypeLabel.setForeground(new Color(245, 240, 255));
        finTypeLabel.setBounds(20, 240, 100, 25);
        customizerPanel.add(finTypeLabel);

        JComboBox<CustomConstants.finType> finTypeCombo = new JComboBox<>(CustomConstants.finType.values());
        finTypeCombo.setSelectedItem(CustomConstants.finType.TRIANGLE);
        finTypeCombo.setBounds(20, 270, 120, 25);
        customizerPanel.add(finTypeCombo);

        JLabel engineLabel = new JLabel("Engine Type:");
        engineLabel.setForeground(new Color(245, 240, 255));
        engineLabel.setBounds(20, 330, 100, 25);
        customizerPanel.add(engineLabel);

        JComboBox<CustomConstants.rocketEngine> engineCombo = new JComboBox<>(CustomConstants.rocketEngine.values());
        engineCombo.setSelectedItem(CustomConstants.rocketEngine.A8_3);
        engineCombo.setBounds(20, 360, 120, 25);
        customizerPanel.add(engineCombo);

        JLabel bodyDensityLabel = new JLabel("Body Density: 1.00x");
        bodyDensityLabel.setForeground(new Color(245, 240, 255));
        bodyDensityLabel.setBounds(20, 390, 180, 25);
        customizerPanel.add(bodyDensityLabel);

        JSlider bodyDensitySlider = new JSlider(10, 300, 100);
        bodyDensitySlider.setBounds(20, 420, 140, 40);
        customizerPanel.add(bodyDensitySlider);

        JLabel noseDensityLabel = new JLabel("Nose Density: 1.00x");
        noseDensityLabel.setForeground(new Color(245, 240, 255));
        noseDensityLabel.setBounds(20, 480, 180, 25);
        customizerPanel.add(noseDensityLabel);

        JSlider noseDensitySlider = new JSlider(10, 300, 100);
        noseDensitySlider.setBounds(20, 510, 140, 40);
        customizerPanel.add(noseDensitySlider);

        JLabel finSizeLabel = new JLabel("Fin Size: 1.00x");
        finSizeLabel.setForeground(new Color(245, 240, 255));
        finSizeLabel.setBounds(20, 580, 180, 25);
        customizerPanel.add(finSizeLabel);

        JSlider finSizeSlider = new JSlider(10, 300, 100);
        finSizeSlider.setBounds(20, 610, 140, 40);
        customizerPanel.add(finSizeSlider);

        JLabel finLengthLabel = new JLabel("Fin Length: 1.00x");
        finLengthLabel.setForeground(new Color(245, 240, 255));
        finLengthLabel.setBounds(20, 680, 180, 25);
        customizerPanel.add(finLengthLabel);

        JSlider finLengthSlider = new JSlider(10, 300, 100);
        finLengthSlider.setBounds(20, 710, 140, 40);
        customizerPanel.add(finLengthSlider);

        JLabel cgLabel = new JLabel("Center of Gravity: ");
        JLabel cpLabel = new JLabel("Center of Pressure: ");
        cgLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        cpLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        cgLabel.setForeground(new Color(245, 240, 255));
        cpLabel.setForeground(new Color(245, 240, 255));
        cgLabel.setBounds(20, 790, 250, 25);
        cpLabel.setBounds(20, 820, 250, 25);
        customizerPanel.add(cgLabel);
        customizerPanel.add(cpLabel);

        JLabel stabilityLabel = new JLabel("Stability: STABLE");
        stabilityLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        stabilityLabel.setForeground(new Color(100, 200, 100));
        stabilityLabel.setBounds(20, 850, 250, 25);
        customizerPanel.add(stabilityLabel);

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

        JButton calcCGButton = new JButton("Calculate Values");
        calcCGButton.setBounds(20, 760, 140, 35);
        customizerPanel.add(calcCGButton);
        calcCGButton.addActionListener(e -> refreshInfo.run());

        JButton saveRocketButton = new JButton("Save Rocket");
        saveRocketButton.setBounds(20, 900, 160, 35);
        saveRocketButton.setBackground(new Color(200, 230, 255));
        saveRocketButton.setForeground(new Color(25, 25, 35));
        customizerPanel.add(saveRocketButton);
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

            JFrame launchFrame = new JFrame("Launching: " + selectedName);
            launchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            launchFrame.setSize(1000, 1000);
            launchFrame.setLocationRelativeTo(null);

            JPanel launchPanel = new JPanel();
            launchPanel.setLayout(null);
            launchPanel.setBackground(new Color(42, 44, 52));
            launchFrame.setContentPane(launchPanel);

            JLabel launchLabel = new JLabel("Custom Rocket Flight");
            launchLabel.setForeground(new Color(245, 240, 255));
            launchLabel.setFont(new Font("Serif", Font.BOLD, 36));
            launchLabel.setBounds(280, 40, 440, 50);
            launchPanel.add(launchLabel);

            JLabel alt = new JLabel("0");
            alt.setForeground(new Color(245, 240, 255));
            alt.setBounds(50, 50, 200, 50);
            launchPanel.add(alt);

            ImageIcon icon = new ImageIcon("Assets/alpha3.png");
            if (!(new java.io.File("Assets/alpha3.png").exists())) {
                icon = new ImageIcon("Assets/tomahawk.png");
            }
            icon.setImage(icon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
            RotatableLabel simRocket = new RotatableLabel(icon);
            simRocket.setSize(100, 100);
            simRocket.setLocation(100, 850 - simRocket.getHeight() + 40);
            launchPanel.add(simRocket);

            JButton exitButton = new JButton("Exit");
            exitButton.setBounds(850, 20, 120, 40);
            exitButton.addActionListener(et -> launchFrame.dispose());
            launchPanel.add(exitButton);

            SimPanel pan = new SimPanel();
            pan.setBounds(0, 0, 1000, 1000);
            pan.setVisible(true);
            launchPanel.add(pan);
            launchPanel.setComponentZOrder(pan, launchPanel.getComponentCount() - 1);

            JLabel customAngleLabel = new JLabel("Launch Angle: 20°");
            customAngleLabel.setForeground(new Color(245, 240, 255));
            customAngleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            customAngleLabel.setBounds(600, 500, 220, 30);
            launchPanel.add(customAngleLabel);

            JSlider customAngleSlider = new JSlider(0, 90, 20);
            customAngleSlider.setBounds(600, 540, 220, 40);
            customAngleSlider.setBackground(new Color(42, 44, 52));
            customAngleSlider.setPaintTicks(true);
            customAngleSlider.setPaintLabels(true);
            customAngleSlider.setMajorTickSpacing(10);
            customAngleSlider.setMinorTickSpacing(5);
            customAngleSlider.addChangeListener(changeEvent -> customAngleLabel.setText("Launch Angle: " + customAngleSlider.getValue() + "°"));
            launchPanel.add(customAngleSlider);

            JButton launchButton = new JButton("Launch");
            launchButton.setBounds(600, 600, 140, 50);
            launchButton.setBackground(new Color(244, 232, 255));
            launchButton.setForeground(new Color(45, 45, 60));
            launchButton.setFocusPainted(false);
            launchButton.setOpaque(true);
            launchButton.addActionListener(evt -> {
                int angle = customAngleSlider.getValue();
                runCustomRocketSim(customRocket, launchPanel, simRocket, alt, pan, exitButton, angle);
            });
            launchPanel.add(launchButton);

            launchFrame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to load rocket: " + ex.getMessage(), "Load failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void runRegularRocketSim(Rocket rocket, JPanel panel, RotatableLabel simRocket, JLabel alt, SimPanel pan, JButton exitButton) {
        final double[] currentStats = new double[5];
        final double[] info = new double[6];
        final double[] scales = new double[2];

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private static Timer startRegularAnimation(Scanner scan, JPanel panel, RotatableLabel simRocket, SimPanel pan, JLabel alt, double[] currentStats) {
        Timer timer = new Timer(10, null);
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

                    int xPosition = (int) (100 + (rawX * (pan.xScale == 0 ? 1 : pan.xScale)));
                    int yPosition = (int) (850 - (rawY * (pan.yScl == 0 ? 1 : pan.yScl)));
                    if (yPosition > 850) yPosition = 850;

                    pan.update((int) vectorMagX, 20, 0, xPosition, yPosition, (int) vectorMagY, 20, 90);
                    pan.repaint();
                    simRocket.setLocation(xPosition, yPosition - simRocket.getHeight() + 40);
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
        final double[] currentStats = new double[5];
        final double[] info = new double[6];
        final double[] scales = new double[2];

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private static Timer startCustomAnimation(Scanner scan, JPanel panel, RotatableLabel simRocket, SimPanel pan, JLabel alt, double[] currentStats) {
        Timer timer = new Timer(10, null);
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

                    int xPosition = (int) (100 + (rawX * (pan.xScale == 0 ? 1 : pan.xScale)));
                    int yPosition = (int) (850 - (rawY * (pan.yScl == 0 ? 1 : pan.yScl)));
                    if (yPosition > 850) yPosition = 850;

                    pan.update((int) vectorMagX, 20, 0, xPosition, yPosition, (int) vectorMagY, 20, 90);
                    pan.repaint();
                    simRocket.setLocation(xPosition, yPosition - simRocket.getHeight() + 40);
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
}
