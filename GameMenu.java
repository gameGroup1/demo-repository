import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMenu extends JFrame {
    public GameMenu() {
        setTitle("Arkanoid - Menu");
        setSize(400, 300);
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

        JButton startBtn = new JButton("Bắt đầu chơi");
        JButton highScoreBtn = new JButton("Điểm Cao Nhất");
        JButton exitBtn = new JButton("Exit");

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        startBtn.setMaximumSize(new Dimension(200, 40));
        highScoreBtn.setMaximumSize(new Dimension(200, 40));
        exitBtn.setMaximumSize(new Dimension(200, 40));

        startBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        highScoreBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(highScoreBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(exitBtn);

        add(panel);

        // Sự kiện nút
        startBtn.addActionListener(e -> {
            // TODO: mở game chính
            JOptionPane.showMessageDialog(this, "Bắt đầu game!");
        });
        highScoreBtn.addActionListener(e -> {
            // TODO: hiển thị điểm cao nhất
            JOptionPane.showMessageDialog(this, "Điểm cao nhất: 0");
        });
        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameMenu().setVisible(true);
        });
    }
}
