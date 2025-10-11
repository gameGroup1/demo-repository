import javax.swing.*;
import javafx.embed.swing.JFXPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class GameMenu extends JFrame {
    private boolean checkStart = false;
    private MainGame mainGame;
    private JLabel title;
    private JButton startBtn;
    private JButton bestScoreBtn;
    private JButton exitBtn;

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

        // Load background
        Image backgroundImage = loadBackgroundImage();
        System.out.println("Background loaded: " + (backgroundImage != null ? "Success" : "Failed"));

        BackgroundPanel panel = new BackgroundPanel(backgroundImage);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // XÓA dòng setBackground để image có thể hiển thị

        title = new JLabel("ARKANOID");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.CYAN);
        title.setOpaque(false); // Làm title trong suốt

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

        // Làm buttons bán trong suốt (optional - để nhìn thấy background)
        makeButtonTransparent(startBtn);
        makeButtonTransparent(bestScoreBtn);
        makeButtonTransparent(exitBtn);

        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bestScoreBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);

        add(panel);

        // Effects
        // startTitleAnimation();
        addButtonEffects(startBtn);
        addButtonEffects(bestScoreBtn);
        addButtonEffects(exitBtn);

        startBtn.addActionListener(e -> {
            checkStart = true;
            setVisible(false);
            startGame();
        });

        bestScoreBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Best Score: 0");
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    // Method để làm button bán trong suốt
    private void makeButtonTransparent(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Nền tối bán trong suốt
        button.setBackground(new Color(20, 40, 30, 10)); // Xanh đen trong suốt
        button.setForeground(new Color(200, 255, 150)); // Chữ xanh lá vàng sáng

        // Viền xanh ngọc sáng
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 255, 200, 150), 2), // Viền cyan TRONG SUỐT (alpha 150)
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    // Method load image
    private Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            // Cách 1: Dùng ClassLoader (tốt nhất)
            java.net.URL imageURL = getClass().getClassLoader().getResource("background.gif");
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
                System.out.println("✓ Loaded from classpath");
                return backgroundImage;
            }

            // Cách 2: Đường dẫn tương đối từ thư mục chạy
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

    // BackgroundPanel - FIXED VERSION
    private static class BackgroundPanel extends JPanel {
        private Image image;

        public BackgroundPanel(Image image) {
            this.image = image;
            setOpaque(true); // Quan trọng
            System.out.println("Panel created with image: " + (image != null));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image != null) {
                // Vẽ image trực tiếp (không dùng getScaledInstance vì nó chậm)
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                System.out.println("Image drawn at " + getWidth() + "x" + getHeight());
            } else {
                // Nếu không có image, vẽ nền đen
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