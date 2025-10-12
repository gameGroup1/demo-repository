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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class GameMenu extends JFrame {
    private boolean checkStart = false;
    private JLabel title;
    private JButton startBtn;
    private JButton bestScoreBtn;
    private JButton exitBtn;
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    private MediaPlayer mediaPlayer;

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
        title.setFont(new Font("Arial", Font.BOLD, 63));
        title.setForeground(Color.CYAN);
        title.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        startBtn = new JButton("Start");
        bestScoreBtn = new JButton("Best Score");
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

        startBtn.setFont(new Font("Arial", Font.PLAIN, 36));
        bestScoreBtn.setFont(new Font("Arial", Font.PLAIN, 36));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 36));

        makeButtonTransparent(startBtn);
        makeButtonTransparent(bestScoreBtn);
        makeButtonTransparent(exitBtn);

        // Add volume slider
        volumeLabel = new JLabel("Volume: " + (int)(SoundManager.getGlobalVolume() * 100) + "%");
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setOpaque(false);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(SoundManager.getGlobalVolume() * 100));
        volumeSlider.setPreferredSize(new Dimension(200, 40));
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.addChangeListener(e -> {
            double volume = volumeSlider.getValue() / 100.0;
            SoundManager.setGlobalVolume(volume);
            volumeLabel.setText("Volume: " + volumeSlider.getValue() + "%");
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(SoundManager.getGlobalVolume());
            }
        });

        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bestScoreBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);
        panel.add(Box.createVerticalGlue()); // Đẩy volumeLabel và volumeSlider xuống dưới cùng
        panel.add(volumeLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(volumeSlider);
        panel.add(Box.createVerticalStrut(20)); // Khoảng cách dưới cùng để tránh sát mép

        add(panel);

        addButtonEffects(startBtn);
        addButtonEffects(bestScoreBtn);
        addButtonEffects(exitBtn);

        startBtn.addActionListener(e -> {
            checkStart = true;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            setVisible(false);
            startGame();
        });

        bestScoreBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Best Score", true);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(new Color(0, 50, 0)); // Nền xanh lá đậm để hòa hợp background

            JLabel label = new JLabel("Best Score: " + MainGame.getBestScore());
            label.setFont(new Font("Arial", Font.BOLD, 32)); // Tăng kích cỡ chữ lên 32
            label.setForeground(Color.CYAN); // Màu cyan nổi bật trên nền xanh lá
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);

            JButton closeBtn = new JButton("Close");
            makeButtonTransparent(closeBtn); // Áp dụng style tương tự các nút khác
            closeBtn.addActionListener(a -> dialog.dispose());

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.add(label, BorderLayout.CENTER);
            contentPanel.add(closeBtn, BorderLayout.SOUTH);

            dialog.add(contentPanel);
            dialog.setVisible(true);
        });

        exitBtn.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
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
            mediaPlayer.setVolume(SoundManager.getGlobalVolume());
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