import javax.swing.*;
import javafx.embed.swing.JFXPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class GameMenu extends JFrame {
    private boolean checkStart = false;
    private MainGame mainGame;
    private JLabel title;
    private JButton startBtn;
    private JButton bestScoreBtn;
    private JButton exitBtn;
    private JSlider backgroundSlider;
    private JLabel backgroundLabel;
    private JSlider effectSlider;
    private JLabel effectLabel;
    private MediaPlayer mediaPlayer;
    private static AudioClip mouseClickSound;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.MouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }

    public void setCheckStart(boolean checkStart) {
        this.checkStart = checkStart;
    }

    public boolean isCheckStart() {
        return checkStart;
    }

    public GameMenu() {
        setTitle("Arkanoid - Start Menu");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JFXPanel jfxPanel = new JFXPanel();

        // Load and play background music
        playBackgroundMusic();

        // Load background
        Image backgroundImage = loadBackgroundImage();
        System.out.println("Background loaded: " + (backgroundImage != null ? "Success" : "Failed"));

        BackgroundPanel panel = new BackgroundPanel(backgroundImage);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        title = new JLabel("ARKANOID");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.CYAN);
        title.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        startBtn = new JButton("Start");
        bestScoreBtn = new JButton("BestScore");
        exitBtn = new JButton("Exit");

        startBtn.setFocusPainted(false);
        bestScoreBtn.setFocusPainted(false);
        exitBtn.setFocusPainted(false);

        Dimension btnSize = new Dimension(200, 40);
        startBtn.setPreferredSize(btnSize);
        bestScoreBtn.setPreferredSize(btnSize);
        exitBtn.setPreferredSize(btnSize);

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestScoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        startBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        bestScoreBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        makeButtonTransparent(startBtn);
        makeButtonTransparent(bestScoreBtn);
        makeButtonTransparent(exitBtn);

        // Add background music volume slider
        backgroundLabel = new JLabel("Background Volume: " + (int)(SoundManager.getBackgroundVolume() * 100) + "%");
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundLabel.setForeground(Color.WHITE);
        backgroundLabel.setOpaque(false);

        backgroundSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(SoundManager.getBackgroundVolume() * 100));
        backgroundSlider.setPreferredSize(new Dimension(200, 40));
        backgroundSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundSlider.setOpaque(false);
        backgroundSlider.setForeground(Color.WHITE);
        backgroundSlider.setMajorTickSpacing(25);
        backgroundSlider.setPaintTicks(true);
        backgroundSlider.addChangeListener(e -> {
            double volume = backgroundSlider.getValue() / 100.0;
            SoundManager.setBackgroundVolume(volume);
            backgroundLabel.setText("Background Volume: " + backgroundSlider.getValue() + "%");
        });

        // Add effect volume slider
        effectLabel = new JLabel("Effect Volume: " + (int)(SoundManager.getEffectVolume() * 100) + "%");
        effectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        effectLabel.setForeground(Color.WHITE);
        effectLabel.setOpaque(false);

        effectSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(SoundManager.getEffectVolume() * 100));
        effectSlider.setPreferredSize(new Dimension(200, 40));
        effectSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectSlider.setOpaque(false);
        effectSlider.setForeground(Color.WHITE);
        effectSlider.setMajorTickSpacing(25);
        effectSlider.setPaintTicks(true);
        effectSlider.addChangeListener(e -> {
            double volume = effectSlider.getValue() / 100.0;
            SoundManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + effectSlider.getValue() + "%");
        });

        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bestScoreBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);
        panel.add(Box.createVerticalGlue());
        panel.add(backgroundLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(backgroundSlider);
        panel.add(Box.createVerticalStrut(10));
        panel.add(effectLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(effectSlider);
        panel.add(Box.createVerticalStrut(20));

        add(panel);

        addButtonEffects(startBtn);
        addButtonEffects(bestScoreBtn);
        addButtonEffects(exitBtn);

        startBtn.addActionListener(e -> {
            checkStart = true;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            setVisible(false);
            startGame();
        });

        bestScoreBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Best Score: 0");
        });

        exitBtn.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            System.exit(0);
        });
    }

    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.menuMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("✓ Playing MenuMusic.wav from classpath");
            } else {
                media = new Media(Path.getFileURL(Path.menuMusic));
                System.out.println("✓ Playing MenuMusic.wav from: " + Path.menuMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("✗ Cannot find MenuMusic.wav at " + Path.menuMusic);
            e.printStackTrace();
        }
    }

    private void makeButtonTransparent(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBackground(new Color(20, 40, 30, 10));
        button.setForeground(new Color(200, 255, 150));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 255, 200, 150), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("background.gif");
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
                System.out.println("✓ Loaded from classpath");
                return backgroundImage;
            }
            String[] paths = {
                "resources/background.gif",
                "./resources/background.gif",
                "../resources/background.gif"
            };
            for (String path : paths) {
                java.io.File file = new java.io.File(path);
                System.out.println("Trying: " + file.getAbsolutePath());
                if (file.exists()) {
                    backgroundImage = new ImageIcon(path).getImage();
                    System.out.println("✓ Loaded from: " + path);
                    return backgroundImage;
                }
            }
            System.err.println("✗ Cannot find background.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }

    private static class BackgroundPanel extends JPanel {
        private Image image;

        public BackgroundPanel(Image image) {
            this.image = image;
            setOpaque(true);
            System.out.println("Panel created with image: " + (image != null));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                System.out.println("Image drawn at " + getWidth() + "x" + getHeight());
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private void startTitleAnimation() {
        Timer timer = new Timer(100, new ActionListener() {
            private float scale = 1.0f;
            private boolean increasing = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (increasing) {
                    scale += 0.02f;
                    if (scale >= 1.2f) {
                        increasing = false;
                    }
                } else {
                    scale -= 0.02f;
                    if (scale <= 0.8f) {
                        increasing = true;
                    }
                }
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                title.setFont(title.getFont().deriveFont(at));
                title.repaint();
            }
        });
        timer.start();
    }

    private void addButtonEffects(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.1, 1.1)));
                button.repaint();
                if (mouseClickSound != null) {
                    mouseClickSound.play(SoundManager.getEffectVolume());
                } else {
                    System.err.println("Mouse_Click.wav not loaded.");
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.0, 1.0)));
                button.repaint();
            }
        });

        button.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.1, 1.1)));
                button.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.0, 1.0)));
                button.repaint();
            }
        });
    }

    private void startGame() {
        MainGame.createAndShowGame();
    }

    public static void showMenu() {
        SwingUtilities.invokeLater(() -> {
            new GameMenu().setVisible(true);
        });
    }
}