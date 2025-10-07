import javax.swing.*;

import javafx.embed.swing.JFXPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class GameMenu extends JFrame {
    private boolean checkStart = false;
    private MainGame mainGame; // Tham chiếu đến game chính
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
        setSize(400, 300); // Giảm kích thước cho phù hợp
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Khởi tạo JavaFX để hỗ trợ launch game
        JFXPanel jfxPanel = new JFXPanel();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        title = new JLabel("ARKANOID");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.CYAN);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        startBtn = new JButton("Start");
        bestScoreBtn = new JButton("BestScore");
        exitBtn = new JButton("Exit");

        startBtn.setFocusPainted(false);

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

        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bestScoreBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);

        add(panel);

        // Áp dụng hiệu ứng co giãn liên tục cho title
      //  startTitleAnimation();

        // Áp dụng hiệu ứng scale khi hover và focus cho các button
        addButtonEffects(startBtn);
        addButtonEffects(bestScoreBtn);
        addButtonEffects(exitBtn);

        // Sửa ActionListener cho nút Start
        startBtn.addActionListener(e -> {
            checkStart = true;
            setVisible(false); // Ẩn menu
            startGame(); // Khởi động game
        });
        
        bestScoreBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Best Score: 0");
        });
        
        exitBtn.addActionListener(e -> System.exit(0));
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
        // Hiệu ứng scale khi hover (chuột)
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

        // Hiệu ứng scale khi focus (bàn phím)
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
        // Sử dụng createAndShowGame thay vì launchGame
        MainGame.createAndShowGame();
    }

    public static void showMenu() {
        SwingUtilities.invokeLater(() -> {
            new GameMenu().setVisible(true);
        });
    }

}