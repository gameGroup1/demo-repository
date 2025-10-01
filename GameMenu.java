import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMenu extends JFrame {
    public GameMenu() {
    // Loại bỏ khung ngoài của nút Start
        setTitle("Arkanoid - Start Menu");
        setSize(1400, 1300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

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

        // Loại bỏ focus rectangle cho nút Start
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

        startBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Game Start!");
        });
        bestScoreBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Best Score: 0");
        });
        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void Start() {
        SwingUtilities.invokeLater(() -> {
            new GameMenu().setVisible(true);
        });
    }
}
