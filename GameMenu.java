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

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.MouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }
    private Font loadCustomFont() {
    try {
        // Thử load từ classpath trước
        java.net.URL fontURL = getClass().getClassLoader().getResource("Monotype_corsiva.ttf");
        if (fontURL != null) {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            System.out.println("✓ Loaded Monotype Corsiva from classpath");
            return customFont.deriveFont(Font.BOLD, 58f); // Size 48, Bold
        }
        
        // Nếu không có trong classpath, thử từ folder resources
        String[] paths = {
            "resources/Monotype_corsiva.ttf",
            "./resources/Monotype_corsiva.ttf",
            "../resources/Monotype_corsiva.ttf"
        };
        
        for (String path : paths) {
            java.io.File fontFile = new java.io.File(path);
            if (fontFile.exists()) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                System.out.println("✓ Loaded Monotype Corsiva from: " + path);
                return customFont.deriveFont(Font.BOLD, 40f);
            }
        }
        
        System.err.println("✗ Cannot find Monotype_corsiva.ttf, using default font");
    } catch (Exception e) {
        System.err.println("✗ Error loading custom font: " + e.getMessage());
        e.printStackTrace();
    }
    // Fallback về Arial nếu không load được
    return new Font("Arial", Font.BOLD, 48);
}
    public GameMenu() {
        setTitle("Arkanoid - Start Menu");
        setSize(1100, 500);
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

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
titlePanel.setOpaque(false); // Giữ trong suốt
title = new JLabel("-ARKANOID-");
title.setFont(loadCustomFont());
title.setForeground(new Color(154, 205, 50));
title.setOpaque(false);
titlePanel.add(title);
panel.add(Box.createVerticalStrut(30));
panel.add(titlePanel);
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

        startBtn.setFont(new Font("Arial", Font.PLAIN, 30));
        bestScoreBtn.setFont(new Font("Arial", Font.PLAIN, 30));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 30));

        makeButtonTransparent(startBtn);
        makeButtonTransparent(bestScoreBtn);
        makeButtonTransparent(exitBtn);

        // Add background music volume slider
        backgroundLabel = new JLabel("Background Volume: " + (int)(SoundManager.getBackgroundVolume() * 100) + "%");
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundLabel.setForeground(new Color(154, 205, 50));
        backgroundLabel.setOpaque(false);

        backgroundSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(SoundManager.getBackgroundVolume() * 100));
        backgroundSlider.setPreferredSize(new Dimension(200, 10));
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
       effectLabel.setForeground(new Color(154, 205, 50));

        effectLabel.setOpaque(false);

        effectSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(SoundManager.getEffectVolume() * 100));
        effectSlider.setPreferredSize(new Dimension(160, 10));
        effectSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectSlider.setOpaque(false);
        effectSlider.setForeground(new Color(154, 205, 50));
        effectSlider.setMajorTickSpacing(15);
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
            JDialog dialog = new JDialog(this, "Best Score", true);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(new Color(0, 50, 0)); // Nền xanh lá đậm để hòa hợp background

            JLabel label = new JLabel("Best Score: " + MainGame.getBestScore());
            label.setFont(new Font("Arial", Font.BOLD, 28)); // Tăng kích cỡ chữ lên 32
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
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setBackground(new Color(20, 40, 30, 100));
        button.setForeground((new Color(154, 200, 50)));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Xóa viền hoàn toàn
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
    // Lưu phông chữ gốc để khôi phục sau này
    Font originalFont = button.getFont();
    float enlargedSize = originalFont.getSize() * 1.05f; // Tăng kích thước phông chữ lên 10%

    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            // Đặt phông chữ lớn hơn khi di chuột vào
            button.setFont(originalFont.deriveFont(enlargedSize));
            button.repaint();
            if (mouseClickSound != null) {
                mouseClickSound.play(SoundManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav không được tải.");
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Khôi phục kích thước phông chữ gốc
            button.setFont(originalFont);
            button.repaint();
        }
    });

    button.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            // Đặt phông chữ lớn hơn khi nút được focus
            button.setFont(originalFont.deriveFont(enlargedSize));
            button.repaint();
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            // Khôi phục kích thước phông chữ gốc
            button.setFont(originalFont);
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