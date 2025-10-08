import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class EndMenu extends JFrame {
    private static int score;
    private static int bestScore;
    private JLabel title;
    private JLabel scoreLabel;
    private JLabel bestScoreLabel;
    private JButton playAgainBtn;
    private JButton exitBtn;

    public EndMenu(int score, int bestScore) {
        EndMenu.score = score;
        EndMenu.bestScore = bestScore;
        
        setTitle("Game Over");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load background
        Image backgroundImage = loadBackgroundImage();
        System.out.println("Background loaded: " + (backgroundImage != null ? "Success" : "Failed"));

        BackgroundPanel panel = new BackgroundPanel(backgroundImage);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Tiêu đề Game Over
        title = new JLabel("GAME OVER");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.RED);
        title.setOpaque(false);
        
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(40));

        // Hiển thị điểm
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setOpaque(false);

        bestScoreLabel = new JLabel("Best Score: " + bestScore);
        bestScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestScoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        bestScoreLabel.setForeground(Color.YELLOW);
        bestScoreLabel.setOpaque(false);

        panel.add(scoreLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(bestScoreLabel);
        panel.add(Box.createVerticalStrut(40));

        // Các nút
        playAgainBtn = new JButton("Play Again");
        exitBtn = new JButton("Exit");

        playAgainBtn.setFocusPainted(false);
        exitBtn.setFocusPainted(false);

        Dimension btnSize = new Dimension(200, 40);
        playAgainBtn.setPreferredSize(btnSize);
        exitBtn.setPreferredSize(btnSize);

        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        playAgainBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        // Làm buttons bán trong suốt
        makeButtonTransparent(playAgainBtn, new Color(76, 175, 80)); // Màu xanh lá
        makeButtonTransparent(exitBtn, new Color(244, 67, 54)); // Màu đỏ

        panel.add(playAgainBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);

        add(panel);

        // Effects
        addButtonEffects(playAgainBtn);
        addButtonEffects(exitBtn);

        // Xử lý sự kiện
        playAgainBtn.addActionListener(e -> {
            setVisible(false);
            dispose();
            startNewGame();
        });
        
        exitBtn.addActionListener(e -> {
            System.exit(0);
        });
    }

    // Method để làm button bán trong suốt với màu tùy chỉnh
    private void makeButtonTransparent(JButton button, Color baseColor) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        // Nền bán trong suốt dựa trên màu cơ sở
        Color backgroundColor = new Color(
            baseColor.getRed(), 
            baseColor.getGreen(), 
            baseColor.getBlue(), 
            100 // Độ trong suốt
        );
        
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        
        // Viền với màu sáng hơn
        Color borderColor = new Color(
            Math.min(baseColor.getRed() + 50, 255),
            Math.min(baseColor.getGreen() + 50, 255), 
            Math.min(baseColor.getBlue() + 50, 255),
            200
        );
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    // Method load image - giống với GameMenu
    private Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            // Cách 1: Dùng ClassLoader (tốt nhất)
            java.net.URL imageURL = getClass().getClassLoader().getResource("end.gif");
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
                System.out.println("✓ Loaded from classpath");
                return backgroundImage;
            }
            
            // Cách 2: Đường dẫn tương đối từ thư mục chạy
            String[] paths = {
                "resources/end.gif",
                "./resources/end.gif",
                "../resources/end.gif"
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
            
            System.err.println("✗ Cannot find end.gif");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }

    // BackgroundPanel - giống với GameMenu
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
                // Nếu không có image, vẽ nền gradient đen
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 40),
                    getWidth(), getHeight(), new Color(40, 20, 20)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // Animation cho tiêu đề - giống với GameMenu

    // Hiệu ứng button - giống với GameMenu
    private void addButtonEffects(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.1, 1.1)));
                // Làm sáng màu nền khi hover
                Color originalBg = button.getBackground();
                Color hoverBg = new Color(
                    Math.min(originalBg.getRed() + 30, 255),
                    Math.min(originalBg.getGreen() + 30, 255),
                    Math.min(originalBg.getBlue() + 30, 255),
                    originalBg.getAlpha()
                );
                button.setBackground(hoverBg);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(AffineTransform.getScaleInstance(1.0, 1.0)));
                // Khôi phục màu nền gốc
                if (button == playAgainBtn) {
                    button.setBackground(new Color(76, 175, 80, 100));
                } else {
                    button.setBackground(new Color(244, 67, 54, 100));
                }
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

    private void startNewGame() {
        // Gọi phương thức khởi tạo game mới
        MainGame.createAndShowGame();
    }

    public static void show(int score, int bestScore) {
        SwingUtilities.invokeLater(() -> {
            new EndMenu(score, bestScore).setVisible(true);
        });
    }
}