import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Random;

public class MainGame {
    private final int widthW = 400;
    private final int heightW = 600;
    private final int widthP = 100;
    private final int heightP = 20;
    private final int radiusB = 7;
    private final int speedB = 5;
    private final int wallThickness = 10;

    private Ball ball;
    private Paddle paddle;
    private Wall leftWall;
    private Wall rightWall;
    private Wall topWall;
    private Bricks[] bricks;
    private Group root;
    private AnimationTimer gameLoop;
    private Stage primaryStage;
    private gameOver gameOver;

    public MainGame() {
        Material[] materials = {Material.rock, Material.metal, Material.wood, Material.jewel};
        Random random = new Random();

        double ballX = (widthW / 2.0) - radiusB;
        double ballY = heightW - heightP - (radiusB * 2);
        ball = new Ball(ballX, ballY, radiusB, speedB, Material.metal);
        ball.setDx(speedB);
        ball.setDy(-speedB);

        double paddleX = (widthW - widthP) / 2.0;
        paddle = new Paddle(paddleX, heightW - heightP, widthP, heightP, Material.wood);

        leftWall = new Wall(0, 0, wallThickness, heightW, Material.metal);
        rightWall = new Wall(widthW - wallThickness, 0, wallThickness, heightW, Material.metal);
        topWall = new Wall(0, 0, widthW, wallThickness, Material.metal);

        bricks = new Bricks[50];
        int brickWidth = 30;
        int brickHeight = 15;
        int spacing = 5;
        int rowCount = 5;
        int colCount = 10;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                double brickX = col * (brickWidth + spacing) + wallThickness + 20;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;
                Material randomMaterial = materials[random.nextInt(materials.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomMaterial);
            }
        }
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gameOver = new gameOver();
        
        root = new Group();
        Scene scene = new Scene(root, widthW, heightW, Color.BLACK);

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        // Thêm sự kiện đóng cửa sổ
        primaryStage.setOnCloseRequest(e -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            // Không exit, chỉ đóng stage
        });
        
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            addGameElementsToRoot();
            setupInput(scene);
            startGameLoop();
        });
        delay.play();
    }

    private void addGameElementsToRoot() {
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
                paddle.move(event, leftWall, rightWall);
            }
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (ball == null) return;

                Update.position(ball);

                if (leftWall != null) {
                    Update.position(ball, leftWall);
                }
                if (rightWall != null) {
                    Update.position(ball, rightWall);
                }
                if (topWall != null) {
                    Update.position(ball, topWall);
                }
                if (paddle != null) {
                    Update.position(ball, paddle);
                }
                if (bricks != null) {
                    Update.position(ball, bricks);
                }

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

                if (paddle != null) {
                    paddle.render();
                }
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

                if (gameOver.isGameOver(ball, heightW)) {
                    // Khi game over, hiển thị EndMenu
                    gameLoop.stop();
                    // Tính score dựa trên số bricks bị phá hủy (giả sử Bricks có method isDestroyed())
                    int destroyedCount = 0;
                    for (Bricks brick : bricks) {
                        if (brick != null && brick.isBreak()) {  // Giả sử có method isDestroyed() trong Bricks
                            destroyedCount++;
                        }
                    }
                    int score = destroyedCount * 10;  // Mỗi brick 10 điểm
                    Platform.runLater(() -> {
                        primaryStage.close();
                        EndMenu.show(score, 0);  // Best score tạm thời là 0
                    });
                }
            }
        };
        gameLoop.start();
    }

    // Phương thức tĩnh để tạo và hiển thị game mới (thay thế cho launch())
    public static void createAndShowGame() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainGame game = new MainGame();
            game.start(stage);
        });
    }

    public static void main(String[] args) {
        // Hiển thị menu trước, game sẽ được khởi chạy từ menu
        GameMenu.showMenu();
    }
}