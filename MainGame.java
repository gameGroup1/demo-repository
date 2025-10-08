/* main - Lớp chính cho game Arkanoid, kế thừa Application để quản lý JavaFX */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import java.util.Random;

public class MainGame extends Application {
    // Hằng số kích thước game (đóng gói để dễ bảo trì)
    private final int widthW = 400;
    private final int heightW = 600;
    private final int widthP = 100;
    private final int heightP = 20;
    private final int radiusB = 7;
    private final int speedB = 5;
    private final int wallThickness = 10; // Độ dày tường (đóng gói)

    // Các đối tượng game (private để đóng gói)
    private Ball ball;
    private Paddle paddle;
    private Wall leftWall; // Tường trái
    private Wall rightWall; // Tường phải
    private Wall topWall; // Tường trên
    private Bricks[] bricks;
    private Group root; // Root cho scene graph
    private AnimationTimer gameLoop;

    // Constructor: Khởi tạo tất cả đối tượng game (áp dụng đóng gói và trừu tượng hóa)
    public MainGame() {
        // Tạo mảng 4 phần tử chứa các Material ngẫu nhiên cho Bricks
        Material[] materials = {Material.rock, Material.metal, Material.wood, Material.jewel};
        Random random = new Random();

        double ballX = (widthW / 2.0) - radiusB;
        double ballY = heightW - heightP - (radiusB * 2);
        ball = new Ball(ballX, ballY, radiusB, speedB, Material.metal);
        ball.setDx(speedB); // Vận tốc X ban đầu (phải)
        ball.setDy(-speedB); // Vận tốc Y ban đầu (lên)

        double paddleX = (widthW - widthP) / 2.0;
        double paddleY = heightW - heightP;
        paddle = new Paddle(paddleX, paddleY, widthP, heightP, Material.wood);

        // Khởi tạo ba bức tường (left, right, top - kế thừa GameObject, phần 5.1)
        leftWall = new Wall(0, 0, wallThickness, heightW, Material.metal); // Tường trái: mỏng, cao toàn màn
        rightWall = new Wall(widthW - wallThickness, 0, wallThickness, heightW, Material.metal); // Tường phải
        topWall = new Wall(0, 0, widthW, wallThickness, Material.metal); // Tường trên: rộng toàn màn, mỏng

        // Khởi tạo mảng Bricks (lưới 5 hàng x 10 cột, phần 4.3.1: hệ thống cấp độ đơn giản)
        bricks = new Bricks[50]; // 5x10 = 50 gạch
        int brickWidth = 30;
        int brickHeight = 15;
        int spacing = 5;
        int rowCount = 5;
        int colCount = 10;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // Tính vị trí Brick (bắt đầu từ x=wallThickness + 10, y=wallThickness + 50 để tránh biên tường)
                double brickX = col * (brickWidth + spacing) + wallThickness + 20;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;
                // Chọn Material ngẫu nhiên từ mảng materials
                Material randomMaterial = materials[random.nextInt(materials.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomMaterial);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Tạo root Group cho scene graph (phần 4.2.1: xây dựng GUI - trừu tượng hóa scene graph)
        root = new Group();

        // Tạo Scene với kích thước canvas và nền đen (màn hình đen ban đầu - phần 4.2.1: giao diện thẩm mỹ)
        Scene scene = new Scene(root, widthW, heightW, Color.BLACK);

        // Thiết lập Stage ban đầu (hiển thị màn hình đen)
        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Không cho thay đổi kích thước (giữ tỷ lệ)
        primaryStage.show();

        // Delay 3 giây trước khi load game (sử dụng PauseTransition - phần 4.2.3: đa luồng với delay)
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            // Sau delay, thêm các đối tượng game vào root và khởi động game loop
            addGameElementsToRoot(); // Phương thức riêng để thêm elements (trừu tượng hóa phần 5.2)
            setupInput(scene); // Phương thức riêng để set input (đóng gói phần 5.2)
            startGameLoop(); // Khởi động AnimationTimer (phần 4.2.3)
        });
        delay.play(); // Bắt đầu delay
    }

    private void addGameElementsToRoot() {
        // Kiểm tra null và thêm Paddle, Ball vào root
        if (paddle != null && paddle.getNode() != null) {
            root.getChildren().add(paddle.getNode());
        }
        if (ball != null && ball.getNode() != null) {
            root.getChildren().add(ball.getNode());
        }

        if (leftWall != null && leftWall.getNode() != null) {
            root.getChildren().add(leftWall.getNode());
        }
        if (rightWall != null && rightWall.getNode() != null) {
            root.getChildren().add(rightWall.getNode());
        }
        if (topWall != null && topWall.getNode() != null) {
            root.getChildren().add(topWall.getNode());
        }

        if (bricks != null) {
            for (Bricks brick : bricks) {
                if (brick != null && brick.getNode() != null) {
                    root.getChildren().add(brick.getNode());
                }
            }
        }
    }

    private void setupInput(Scene scene) {
        scene.setOnMouseMoved(event -> {
            if (paddle != null && leftWall != null && rightWall != null) {
                paddle.move(event, leftWall, rightWall); // Truyền left/right wall để giới hạn
            }
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (ball == null) return;
                Update.position(ball);

                if (leftWall != null) Update.position(ball, leftWall);
                if (rightWall != null) Update.position(ball, rightWall);
                if (topWall != null) Update.position(ball, topWall);
                if (paddle != null) Update.position(ball, paddle);
                if (bricks != null) Update.position(ball, bricks);

                if (paddle != null && leftWall != null && rightWall != null) {
                    double leftBound = leftWall.getX() + leftWall.getWidth();
                    double rightBound = rightWall.getX();
                    if (paddle.getX() < leftBound) {
                        paddle.setX(leftBound);
                    }
                    if (paddle.getX() + paddle.getWidth() > rightBound) {
                        paddle.setX(rightBound - paddle.getWidth());
                    }
                }

                if (paddle != null) paddle.render();
                ball.render();
                if (leftWall != null) leftWall.render();
                if (rightWall != null) rightWall.render();
                if (topWall != null) topWall.render();
                if (bricks != null) {
                    for (Bricks brick : bricks) {
                        if (brick != null) {
                            brick.render();
                        }
                    }
                }

                // Xử lý lỗi: Nếu Ball rơi dưới màn hình (mất mạng, thoát game - phần 4.1.1)
                if (ball.getY() > heightW + radiusB) { // + radiusB để Ball hoàn toàn rơi
                    System.exit(0);
                }
            }
        };
        gameLoop.start(); // Bắt đầu game loop sau delay
    }

    // Main method: Khởi chạy Application (phần 4.1.2: cài đặt)
    public static void main(String[] args) {
        launch(args);
    }
}