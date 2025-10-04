import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameFrame extends JFrame {
    private final int widthW = 400;
    private final int heightW = 600;
    private final int widthP = 100;
    private final int heightP = 20;
    private final int radiusB = 15;
    private final int speedB = 2;

    private Ball ball;
    private Paddle paddle;
    private Wall wall;
    private Bricks[] bricks;
    private update updater;
    private checkCollision collisionChecker;
    private gameOver gameOverChecker;
    private Timer gameTimer;
    private boolean gameRunning = true;

    public GameFrame() {
        setTitle("Arkanoid Game");
        setSize(widthW, heightW);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Khởi tạo các đối tượng game
        ball = new Ball((widthW - widthP)/2 + widthP/2, heightW - radiusB - heightP, radiusB, speedB);
        ball.setDx(speedB);
        ball.setDy(-speedB);
        paddle = new Paddle((widthW - widthP)/2, heightW - heightP, widthP, heightP);
        wall = new Wall(0, 0, widthW, heightW);
        bricks = createBricks();
        updater = new update();
        collisionChecker = new checkCollision();
        gameOverChecker = new gameOver();

        // Thêm panel game
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        // Trình nghe chuyển động chuột cho paddle
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updater.updatePaddlePosition(paddle, e.getX() - widthP/2, wall, collisionChecker);
            }
        });

        // Vòng lặp game
        gameTimer = new Timer(16, e -> { // ~60 FPS
            if (gameRunning) {
                updater.updateBallPosition(ball);
                updater.updateBW(ball, wall, collisionChecker);
                updater.updateBP(ball, paddle, collisionChecker);
                updater.updateBB(ball, bricks, collisionChecker);

                if (gameOverChecker.isGameOver(ball, heightW)) {
                    gameRunning = false;
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    dispose();
                }

                repaint();
            }
        });
        gameTimer.start();
    }

    private Bricks[] createBricks() {
        List<Bricks> brickList = new ArrayList<>();
        int brickWidth = 50;
        int brickHeight = 20;
        int rows = 5;
        int cols = widthW / brickWidth;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * brickWidth;
                int y = row * brickHeight + 50; // Độ lệch từ trên xuống
                brickList.add(new Bricks(x, y, brickWidth, brickHeight));
            }
        }
        return brickList.toArray(new Bricks[0]);
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

            // Vẽ bóng
            g.setColor(Color.WHITE);
            g.fillOval((int)(ball.getX() - ball.getRadius()), (int)(ball.getY() - ball.getRadius()),
                       (int)(2 * ball.getRadius()), (int)(2 * ball.getRadius()));

            // Vẽ paddle
            g.setColor(Color.BLUE);
            g.fillRect((int)paddle.getX(), (int)paddle.getY(), (int)paddle.getWidth(), (int)paddle.getHeight());

            // Vẽ gạch
            g.setColor(Color.RED);
            for (Bricks brick : bricks) {
                if (!brick.isBreak) {
                    g.fillRect((int)brick.getX(), (int)brick.getY(), (int)brick.getWidth(), (int)brick.getHeight());
                }
            }
        }
    }
}