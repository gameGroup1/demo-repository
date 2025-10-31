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
    private boolean checkStart = false; // Kiểm tra đã nhấn Start chưa
    private JLabel title;               // Tiêu đề "-ARKANOID-"
    private JComponent startBtn;           // Nút bắt đầu game
    private JComponent bestScoreBtn;       // Nút xem điểm cao nhất
    private JComponent exitBtn;            // Nút thoát game
    private JSlider backgroundSlider;   // Thanh điều chỉnh âm lượng nhạc nền
    private JLabel backgroundLabel;     // Nhãn hiển thị % âm lượng nhạc nền
    private JSlider effectSlider;       // Thanh điều chỉnh âm lượng hiệu ứng
    private JLabel effectLabel;         // Nhãn hiển thị % âm lượng hiệu ứng
    private MediaPlayer mediaPlayer;    // Đối tượng phát nhạc nền menu
    private static AudioClip mouseClickSound; // Âm thanh khi di chuột vào nút
    private Image greenButtonImage;
    
    // Khởi tạo âm thanh click chuột (chỉ chạy 1 lần khi class được nạp)
    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }
    public static AudioClip mouseClickSound() {
        return mouseClickSound;
    }
    // Tải font chữ tùy chỉnh "Monotype Corsiva" từ nhiều nguồn
    private Font loadCustomFont() {
        try {
            // 1. Thử tải từ classpath (trong JAR)
            java.net.URL fontURL = getClass().getClassLoader().getResource("Monotype_corsiva.ttf");
            if (fontURL != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                System.out.println("Đã tải font từ classpath");
                return customFont.deriveFont(Font.BOLD, 48f); // Kích thước 48, đậm
            }
            
            // 2. Nếu không có trong classpath → thử từ file hệ thống
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
                    System.out.println("Đã tải font từ: " + path);
                    return customFont.deriveFont(Font.BOLD, 40f);
                }
            }
            
            System.err.println("Không tìm thấy Monotype_corsiva.ttf → dùng font mặc định");
        } catch (Exception e) {
            System.err.println("Lỗi tải font: " + e.getMessage());
            e.printStackTrace();
        }
        // Fallback: dùng Arial nếu không tải được
        return new Font("Arial", Font.BOLD, 48);
    }

    private Image loadButtonImage() {
        try {
            URL imageURL = getClass().getClassLoader().getResource(Path.greenButton.substring(1));
            if (imageURL != null) {
                return new ImageIcon(imageURL).getImage();
            }
            java.io.File file = new java.io.File("resources/green_button.png");
            if (file.exists()) {
                return new ImageIcon(file.getPath()).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Khởi tạo nút hình ảnh với hiệu ứng
    private JComponent createImageButton(String text, Font font) {
    ImageButton imgBtn = new ImageButton(greenButtonImage, text, font);
    return imgBtn; // Bây giờ hợp lệ: ImageButton là JComponent
    }

    private void showBestScoreDialog() {
        JDialog dialog = new JDialog(this, "Best Score", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(0, 50, 0));

        JLabel label = new JLabel("Best Score: " + MainGame.getBestScore());
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.CYAN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);

        JButton closeBtn = new JButton("Close");
        makeButtonTransparent(closeBtn);
        closeBtn.addActionListener(a -> dialog.dispose());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.add(closeBtn, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    // Constructor: khởi tạo giao diện menu
    public GameMenu() {
        setTitle("Arkanoid - Start Menu");
        setSize(1100, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setResizable(false);
        new JFXPanel(); // Khởi động JavaFX (cần thiết để dùng MediaPlayer)

        // Phát nhạc nền menu
        playBackgroundMusic();
        //load nút hình ảnh
        greenButtonImage = loadButtonImage();
        // Tải hình nền
        Image backgroundImage = loadBackgroundImage();
        System.out.println("Hình nền: " + (backgroundImage != null ? "Tải thành công" : "Thất bại"));

        // Panel chính có hình nền
        BackgroundPanel panel = new BackgroundPanel(backgroundImage);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // === TIÊU ĐỀ ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        title = new JLabel("-ARKANOID-");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(loadCustomFont());
        title.setForeground(new Color(154, 205, 50)); // Màu xanh lá
        title.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        if (greenButtonImage == null) {
            System.err.println("Không tải được green_button.png → dùng nút mặc định");
            // Fallback: dùng nút cũ nếu lỗi
        }
        // === CÁC NÚT ===
                // === TẠO NÚT HÌNH ẢNH ===
        Font buttonFont = new Font("Arial", Font.BOLD, 28);

        if (greenButtonImage != null) {
            startBtn = createImageButton("Start", buttonFont);
            bestScoreBtn = createImageButton("Best Score", buttonFont);
            exitBtn = createImageButton("Exit", buttonFont);
        } else {
            // Fallback: dùng nút cũ nếu không tải được ảnh
            startBtn = new JButton("Start");
            bestScoreBtn = new JButton("Best Score");
            exitBtn = new JButton("Exit");
            makeButtonTransparent(startBtn);
            makeButtonTransparent(bestScoreBtn);
            makeButtonTransparent(exitBtn);
            startBtn.setFont(buttonFont);
            bestScoreBtn.setFont(buttonFont);
            exitBtn.setFont(buttonFont);
        }

        // Căn giữa
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestScoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tắt viền focus
        startBtn.setFocusable(false);
        bestScoreBtn.setFocusable(false);
        exitBtn.setFocusable(false);

        // Kích thước nút
        //Dimension btnSize = new Dimension(200, 40);
        //startBtn.setPreferredSize(btnSize);
        //bestScoreBtn.setPreferredSize(btnSize);
        //exitBtn.setPreferredSize(btnSize);

        // Căn giữa
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bestScoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Font chữ nút
        startBtn.setFont(new Font("Arial", Font.PLAIN, 30));
        bestScoreBtn.setFont(new Font("Arial", Font.PLAIN, 30));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 30));

        // Làm nút trong suốt + màu chữ
        makeButtonTransparent(startBtn);
        makeButtonTransparent(bestScoreBtn);
        makeButtonTransparent(exitBtn);

        // === THANH ÂM LƯỢNG NHẠC NỀN ===
        backgroundLabel = new JLabel("Background Volume: " + (int)(SoundManager.getBackgroundVolume() * 100) + "%");
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundLabel.setForeground(new Color(154, 205, 50));
        backgroundLabel.setOpaque(false);

        backgroundSlider = new JSlider(0, 100, (int)(SoundManager.getBackgroundVolume() * 100));
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

        // === THANH ÂM LƯỢNG HIỆU ỨNG ===
        effectLabel = new JLabel("Effect Volume: " + (int)(SoundManager.getEffectVolume() * 100) + "%");
        effectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        effectLabel.setForeground(new Color(154, 205, 50));
        effectLabel.setOpaque(false);

        effectSlider = new JSlider(0, 100, (int)(SoundManager.getEffectVolume() * 100));
        effectSlider.setPreferredSize(new Dimension(160, 40));
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

        // === THÊM VÀO PANEL ===
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

                // === GÁN SỰ KIỆN CHO CÁC NÚT (HỖ TRỢ CẢ ImageButton VÀ JButton) ===
        if (startBtn instanceof ImageButton) {
            ((ImageButton) startBtn).addActionListener(e -> {
                checkStart = true;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    SoundManager.unregisterMediaPlayer(mediaPlayer);
                }
                setVisible(false);
                startGame();
            });
        } else {
            ( (ImageButton) startBtn).addActionListener(e -> {
                checkStart = true;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    SoundManager.unregisterMediaPlayer(mediaPlayer);
                }
                setVisible(false);
                startGame();
            });
        }

        if (bestScoreBtn instanceof ImageButton) {
            ((ImageButton) bestScoreBtn).addActionListener(e -> showBestScoreDialog());
        } else {
            ((ImageButton) bestScoreBtn).addActionListener(e -> showBestScoreDialog());
        }

        if (exitBtn instanceof ImageButton) {
            ((ImageButton) exitBtn).addActionListener(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    SoundManager.unregisterMediaPlayer(mediaPlayer);
                }
                System.exit(0);
            });
        } else {
            ((ImageButton) exitBtn).addActionListener(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    SoundManager.unregisterMediaPlayer(mediaPlayer);
                }
                System.exit(0);
            });
        }
    }


    // Phát nhạc nền menu (lặp vô hạn)
    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.menuMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("Phát nhạc từ classpath");
            } else {
                media = new Media(Path.getFileURL(Path.menuMusic));
                System.out.println("Phát nhạc từ: " + Path.menuMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không tìm thấy MenuMusic.wav: " + Path.menuMusic);
            e.printStackTrace();
        }
    }

    // Làm nút trong suốt, có nền mờ + màu chữ
   private void makeButtonTransparent(JComponent btn) {
    btn.setOpaque(false);
    btn.setBackground(new Color(20, 40, 30, 100)); // nền mờ
    btn.setForeground(new Color(154, 200, 50));    // màu chữ
    btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

    // Chỉ áp dụng cho AbstractButton (JButton, etc.)
    if (btn instanceof AbstractButton) {
        AbstractButton ab = (AbstractButton) btn;
        ab.setContentAreaFilled(false);
        ab.setBorderPainted(false);
    }
    // ImageButton không cần setContentAreaFilled → bỏ qua
    }

    // Tải hình nền từ classpath hoặc file
    private Image loadBackgroundImage() {
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("background.gif");
            if (imageURL != null) {
                Image img = new ImageIcon(imageURL).getImage();
                System.out.println("Tải hình nền từ classpath");
                return img;
            }
            String[] paths = { "resources/background.gif", "./resources/background.gif", "../resources/background.gif" };
            for (String path : paths) {
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    Image img = new ImageIcon(path).getImage();
                    System.out.println("Tải hình nền từ: " + path);
                    return img;
                }
            }
            System.err.println("Không tìm thấy background.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Panel có hình nền đầy màn hình
    private static class BackgroundPanel extends JPanel {
        private Image image;

        public BackgroundPanel(Image image) {
            this.image = image;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this); // Kéo giãn
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // Hiệu ứng tiêu đề nhấp nhô (zoom in/out)
    private void startTitleAnimation() {
        Timer timer = new Timer(100, new ActionListener() {
            private float scale = 1.0f;
            private boolean increasing = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (increasing) {
                    scale += 0.02f;
                    if (scale >= 1.2f) increasing = false;
                } else {
                    scale -= 0.02f;
                    if (scale <= 0.8f) increasing = true;
                }
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                title.setFont(title.getFont().deriveFont(at));
                title.repaint();
            }
        });
        timer.start();
    }

    // Hiệu ứng nút: phóng to chữ + âm thanh khi di chuột vào
    private void addButtonEffects(JComponent startBtn2) {
        Font originalFont = startBtn2.getFont();
        float enlargedSize = originalFont.getSize() * 1.5f;

        startBtn2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startBtn2.setFont(originalFont.deriveFont(enlargedSize));
                startBtn2.repaint();
                if (mouseClickSound != null) {
                    mouseClickSound.play(SoundManager.getEffectVolume());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startBtn2.setFont(originalFont);
                startBtn2.repaint();
            }
        });

        startBtn2.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                startBtn2.setFont(originalFont.deriveFont(enlargedSize));
                startBtn2.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                startBtn2.setFont(originalFont);
                startBtn2.repaint();
            }
        });
    }

    // Bắt đầu game (gọi từ MainGame)
    private void startGame() {
        MainGame.createAndShowGame();
    }
    // Hiển thị menu (gọi từ main)
    public static void showMenu() {
        SwingUtilities.invokeLater(() -> {
            GameMenu menu = new GameMenu();
            menu.setVisible(true);
            // menu.startTitleAnimation(); // Bật nếu muốn hiệu ứng tiêu đề nhấp nhô
        });
    }  
}  // Kết thúc class GameMenu (đây là } cuối cùng của file)