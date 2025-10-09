import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.input.KeyCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainGame {
    private final int widthW = 1080;
    private final int heightW = 720;
    private final int widthP = 150;
    private final int heightP = 30;
    private final int radiusB = 10;
    private final int speedB = 7;
    private final int speedC = 5;
    private final int wallThickness = 30;

    private Ball ball; // Main ball reference for convenience
    private List<Capsule> capsules = new ArrayList<>();
    private Paddle paddle;
    private Wall leftWall;
    private Wall rightWall;
    private Wall topWall;
    private Bricks[] bricks;
    private Group root;
    private AnimationTimer gameLoop;
    private Stage primaryStage;
    private int score = 0;
    private static int highestScore;

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

        leftWall = new Wall(0, 0, wallThickness, heightW, Material.wood);
        rightWall = new Wall(widthW - wallThickness, 0, wallThickness, heightW, Material.wood);
        topWall = new Wall(0, 0, widthW, wallThickness, Material.wood);

        bricks = new Bricks[50];
        int brickWidth = 90;
        int brickHeight = 30;
        int spacing = 5;
        int rowCount = 5;
        int colCount = 10;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                double brickX = col * (brickWidth + spacing) + wallThickness + 30;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;
                Material randomMaterial = materials[random.nextInt(materials.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomMaterial);
            }
        }
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

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
            saveHighestScore(); // Lưu highestScore khi đóng cửa sổ
        });

        primaryStage.show();
        Material.preloadSounds();

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

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Pause.show(primaryStage, gameLoop);
            }
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update capsules
                for (Capsule cap : new ArrayList<>(capsules)) {
                    if (!cap.isVisible()) continue;

                    Update.position(cap);
                    if (cap.getY() + cap.getHeight() > heightW) {
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                        capsules.remove(cap);
                    }
                    if (Collision.check(paddle, cap)) {
                        applyEffect(cap);
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                        capsules.remove(cap);
                    }
                    cap.render();
                }

                Update.position(ball);
                Update.position(ball, leftWall);
                Update.position(ball, rightWall);
                Update.position(ball, topWall);
                Update.position(ball, paddle);

                Bricks hitBrick = Update.position(ball, bricks);
                if (hitBrick != null && hitBrick.isBreak()) {
                    score += 10;
                    highestScore = Math.max(score, highestScore);
                    root.getChildren().remove(hitBrick.getNode());
                    Random rand = new Random();
                    if (rand.nextDouble() < 0.3) {
                        Capsule cap = EffectManager.getCapsule(hitBrick.getX(), hitBrick.getY(), hitBrick.getWidth(), hitBrick.getHeight(), speedC);
                        cap.setVisible(true);
                        root.getChildren().add(cap.getNode());
                        capsules.add(cap);
                    }
                }

                if (ball != null) ball.render();
                if (paddle != null) paddle.render();
                if (leftWall != null) leftWall.render();
                if (rightWall != null) rightWall.render();
                if (topWall != null) topWall.render();
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak()) brick.render();
                }

                if (ball.getY() > heightW) {
                    gameLoop.stop();
                    saveHighestScore(); // Lưu highestScore khi game over
                    Platform.runLater(() -> {
                        primaryStage.close();
                        EndMenu.show(score, highestScore);  // Truyền highestScore thực tế
                    });
                }
            }
        };
        gameLoop.start();
    }

    private void applyEffect(Capsule capsule) {
        if (capsule.equals(EffectManager.inc10PointCapsule)) score += 10;
        else if (capsule.equals(EffectManager.dec10PointCapsule)) score -= 10;
        else if (capsule.equals(EffectManager.inc50PointCapsule)) score += 50;
        else if (capsule.equals(EffectManager.dec50PointCapsule)) score -= 50;
        else if (capsule.equals(EffectManager.inc100PointCapsule)) score += 100;
        else if (capsule.equals(EffectManager.dec100PointCapsule)) score -= 100;
        else if (capsule.equals(EffectManager.fastBallCapsule)) {
            EffectManager.updateSpeed(ball, 1.5);
        }
        else if (capsule.equals(EffectManager.slowBallCapsule)) {
            EffectManager.updateSpeed(ball, 0.5);
        }
       /* else if (capsule) {
            for (Ball b : activeBalls) EffectManager.updatePower(ball, 2.0);
        }
        else if (capsule == toxicBallCapsule) {
            for (Ball b : activeBalls) EffectManager.updatePower(b, 0.5);
        } */
        else if (capsule.equals(EffectManager.powerBallCapsule)) {
            EffectManager.updatePower(ball, 3.0);
        }
        else if (capsule.equals(EffectManager.expandPaddleCapsule)) {
            EffectManager.changeWidth(paddle, 1.5);
        }
        else if (capsule.equals(EffectManager.shrinkPaddleCapsule)) {
            EffectManager.changeWidth(paddle, 0.5);
        }

        highestScore = Math.max(score, highestScore);
    }

    // Phương thức lưu highestScore vào file
    private static void saveHighestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Path.highestScore))) {
            writer.write(String.valueOf(highestScore));
            System.out.println("Đã lưu highestScore: " + highestScore); // Optional: Log để debug
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try (BufferedReader reader = new BufferedReader(new FileReader(Path.highestScore))) {
            String line = reader.readLine();
            if (line != null) highestScore = Integer.parseInt(line.trim()); // Chuyển chuỗi thành số nguyên
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        GameMenu.showMenu();
    }
}