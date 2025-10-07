import javax.swing.*;
import java.awt.*;

import javafx.embed.swing.JFXPanel;

public class GameMenu extends JFrame {
    private boolean checkStart = false;
    private MainGame mainGame; // Tham chiếu đến game chính

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

        JLabel title = new JLabel("ARKANOID");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.CYAN);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        JButton startBtn = new JButton("Start");
        JButton bestScoreBtn = new JButton("BestScore");
        JButton exitBtn = new JButton("Exit");

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