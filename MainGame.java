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
    private final int speedB = 10;
    private final int speedC = 5;
    private final int wallThickness = 30;

    private Ball ball; // Main ball reference for convenience
    private Paddle paddle;
    private Wall leftWall;
    private Wall rightWall;
    private Wall topWall;
    private Bricks[] bricks;
    private Capsule[] capsules;
    private List<Integer> capsuleIndex = new ArrayList<>();
    private Group root;
    private AnimationTimer gameLoop;
    private Stage primaryStage;
    private int score = 0;
    private static int highestScore;

    public MainGame() {
        int[] hardnessArray = {1, 2, 3, 4};
        Random random = new Random();

        double ballX = (widthW / 2.0) - radiusB;
        double ballY = heightW - heightP - (radiusB * 2);
        ball = new Ball(ballX, ballY, radiusB, speedB);
        ball.setDx(speedB);
        ball.setDy(-speedB);

        double paddleX = (widthW - widthP) / 2.0;
        paddle = new Paddle(paddleX, heightW - heightP, widthP, heightP, Color.BROWN);

        leftWall = new Wall(0, 0, wallThickness, heightW, Color.WHITE);
        rightWall = new Wall(widthW - wallThickness, 0, wallThickness, heightW, Color.WHITE);
        topWall = new Wall(0, 0, widthW, wallThickness, Color.WHITE);

        bricks = new Bricks[50];
        capsules = new Capsule[50];
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
                int randomHardness = hardnessArray[random.nextInt(hardnessArray.length)];
                double chance = random.nextDouble();
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomHardness);
                if (chance < 0.3) { // 30% chance to have a capsule
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false); // Initially invisible
                    capsuleIndex.add(index);
                } else {
                    capsules[index] = null; // No capsule for this brick
                }
            }
        }

        preloadSounds();
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

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
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
                // Update capsules first
                for (int index : capsuleIndex) {
                    Capsule cap = capsules[index];
                    if (cap == null || !cap.isVisible()) continue;
                    Update.position(cap);
                    if (cap.getY() + cap.getHeight() > heightW) {
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                    }
                    if (Collision.check(paddle, cap)) {
                        applyEffect(cap);
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                    }
                    cap.render();
                }

                // Update ball with sub-stepping to prevent tunneling
                double ballSpeed = ball.getSpeed();
                int subSteps = (int) Math.ceil(ballSpeed / 5.0); // Adjust step size as needed, e.g., 5 pixels per sub-step
                if (subSteps < 1) subSteps = 1;

                for (int s = 0; s < subSteps; s++) {
                    double stepDx = ball.getDx() / subSteps;
                    double stepDy = ball.getDy() / subSteps;

                    ball.setX(ball.getX() + stepDx);
                    ball.setY(ball.getY() + stepDy);

                    // Check collisions in each sub-step
                    Update.position(ball, leftWall);
                    Update.position(ball, rightWall);
                    Update.position(ball, topWall);
                    Update.position(ball, paddle);

                    int breakIndex = Update.position(ball, bricks);
                    if (breakIndex != -1 && bricks[breakIndex].isBreak()) {
                        score += 10;
                        highestScore = Math.max(score, highestScore);
                        root.getChildren().remove(bricks[breakIndex].getNode());
                        bricks[breakIndex] = null; // Remove reference to broken brick
                        if (capsules[breakIndex] != null && !capsules[breakIndex].isVisible()) {
                            Capsule cap = capsules[breakIndex];
                            if (!root.getChildren().contains(cap.getNode())) {
                                root.getChildren().add(cap.getNode());
                            }
                            cap.setVisible(true);
                        }
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
                    Update.loseLifeSound.play();
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
        String type = capsule.getEffectType();  // Sử dụng type thay vì equals object
        if (type.equals("inc10Point")) score += 10;
        else if (type.equals("dec10Point")) score -= 10;
        else if (type.equals("inc50Point")) score += 50;
        else if (type.equals("dec50Point")) score -= 50;
        else if (type.equals("inc100Point")) score += 100;
        else if (type.equals("dec100Point")) score -= 100;
        else if (type.equals("fastBall")) {
            EffectManager.updateSpeed(ball, 1.5);
        }
        else if (type.equals("slowBall")) {
            EffectManager.updateSpeed(ball, 0.5);
        }
        /* else if (type.equals("toxicBall")) {  // Nếu có từ comment
            for (Ball b : activeBalls) EffectManager.updatePower(b, 0.5);
        } */
        else if (type.equals("powerBall")) {
            EffectManager.updatePower(ball, 3.0);
        }
        else if (type.equals("expandPaddle")) {
            EffectManager.changeWidth(paddle, 1.5);
        }
        else if (type.equals("shrinkPaddle")) {
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

    private void preloadSounds() {
        for(Capsule cap : capsules) {
            if (cap != null) {
                cap.playSound(0.0);
            }
        }
        Update.loseLifeSound.play(0.0);
        Update.brickBreakSound.play(0.0);
        Collision.ballBrickSound.play(0.0);
        Collision.ballPaddleSound.play(0.0);
        Collision.ballWallSound.play(0.0);
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