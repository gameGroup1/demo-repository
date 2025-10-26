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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainGame {
    private final int widthW = 1080;
    private final int heightW = 720;
    private final int widthP = 150;
    private final int heightP = 30;
    private final int radiusB = 15;
    private final int speedB = 5;
    private final int speedC = 2;
    private final int wallThickness = 30;

    private Ball ball;
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
    private MediaPlayer mediaPlayer;
    private boolean isAttached = true;
    private int lives = 5;
    private List<ImageView> heartImages = new ArrayList<>();
    private Text scoreText;
    private Image heartImage;
    private Image collisionImage;

    public MainGame() {
        int[] hardnessArray = {1, 2, 3, 4};
        Random random = new Random();
        collisionImage = new Image("file:resources/boom_collision.gif");
        double paddleX = (widthW - widthP) / 2.0;
        double paddleY = heightW - heightP;

        paddle = new Paddle(paddleX, paddleY, widthP, heightP);
        double centerX = paddleX + widthP / 2;
        double centerY = paddleY - radiusB;

        ball = new Ball(centerX, centerY, radiusB, speedB);
        ball.setDx(0);
        ball.setDy(0);

        leftWall = new Wall("left", 0, 0, wallThickness, heightW, wallThickness);
        rightWall = new Wall("right", widthW - wallThickness, 0, wallThickness, heightW, wallThickness);
        topWall = new Wall("top", 0, 0, widthW, wallThickness, wallThickness);

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
                if (chance < 0.3) {
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                } else {
                    chance = random.nextDouble();
                    if(chance < 0.2) {
                        capsules[index] = new Capsule(Path.explosionCapsule, Path.explosionSound);
                        capsules[index].init(brickX, brickY, brickWidth, brickHeight, 2 * speedC, "explosion");
                        capsules[index].setVisible(false);
                        capsuleIndex.add(index);
                    }
                    else capsules[index] = null;
                }
            }
        }

        Image heartImage = new Image("file:resources/heart.png");

        for (int i = 0; i < lives; i++) {
            ImageView iv = new ImageView(heartImage);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setX(widthW - wallThickness - 80 - i * 36);
            iv.setY(wallThickness + 5);
            heartImages.add(iv);
        }

        // Create score text
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(36));
        scoreText.setX(widthW - wallThickness - 200);
        scoreText.setY(wallThickness + 64);
    }

    private void showBrickCollisionEffect(double x, double y) {
        ImageView effect = new ImageView(collisionImage);
        effect.setFitWidth(60);
        effect.setFitHeight(60);
        effect.setX(x - 30);  // Căn giữa
        effect.setY(y - 30);
        root.getChildren().add(effect);

        // Xóa sau 1 giây (độ dài GIF)
        PauseTransition remove = new PauseTransition(Duration.seconds(1.0));
        remove.setOnFinished(e -> root.getChildren().remove(effect));
        remove.play();
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new Group();
        Scene scene = new Scene(root, widthW, heightW, Color.BLACK);

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            saveHighestScore();
        });

        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            addGameElementsToRoot();
            setupInput(scene);
            startGameLoop();
            playBackgroundMusic();
        });
        delay.play();
    }

    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.backgroundMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("✓ Playing BackgroundMusic.wav from classpath");
            } else {
                media = new Media(Path.getFileURL(Path.backgroundMusic));
                System.out.println("✓ Playing BackgroundMusic.wav from: " + Path.backgroundMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("✗ Cannot find BackgroundMusic.wav at " + Path.backgroundMusic);
            e.printStackTrace();
        }
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

        // Add score text and hearts
        root.getChildren().add(scoreText);
        for (ImageView heart : heartImages) {
            root.getChildren().add(heart);
        }
    }

    private void setupInput(Scene scene) {
        scene.setOnMouseMoved(event -> {
            if (paddle != null && leftWall != null && rightWall != null) {
                paddle.move(event, leftWall, rightWall);
            }
        });
        scene.setOnMouseClicked(event -> {
            if (isAttached) {
                isAttached = false;
                ball.setDy(-speedB);
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

                if (isAttached) {
                    double centerX = paddle.getX() + widthP / 2;
                    double centerY = paddle.getY() - radiusB;
                    ball.setX(centerX);
                    ball.setY(centerY);
                    ball.setDx(0);
                    ball.setDy(0);
                } else {
                    double ballSpeed = ball.getSpeed();
                    int subSteps = (int) Math.ceil(ballSpeed / 5.0);
                    if (subSteps < 1) subSteps = 1;

                    for (int s = 0; s < subSteps; s++) {
                        double stepDx = ball.getDx() / subSteps;
                        double stepDy = ball.getDy() / subSteps;

                        ball.setX(ball.getX() + stepDx);
                        ball.setY(ball.getY() + stepDy);

                        Update.position(ball, leftWall);
                        Update.position(ball, rightWall);
                        Update.position(ball, topWall);
                        Update.position(ball, paddle);
        int breakIndex = Update.position(ball, bricks);
        if (breakIndex != -1 && bricks[breakIndex].isBreak()) {
            score += 10;
            highestScore = Math.max(score, highestScore);

            // Lấy tọa độ brick để đặt hiệu ứng
            Bricks brokenBrick = bricks[breakIndex];
            double brickCenterX = brokenBrick.getX() + brokenBrick.getWidth() / 2;
            double brickCenterY = brokenBrick.getY() + brokenBrick.getHeight() / 2;

            // Chỉ hiển thị hiệu ứng nếu bóng KHÔNG phải fireball
            if (ball.getPower() == 1) { // hoặc kiểm tra !ball.isFireBall() nếu bạn thêm getter
                showBrickCollisionEffect(brickCenterX, brickCenterY);
            }

            root.getChildren().remove(bricks[breakIndex].getNode());
            bricks[breakIndex] = null;

            if (capsules[breakIndex] != null && !capsules[breakIndex].isVisible()) {
                Capsule cap = capsules[breakIndex];
                if (!root.getChildren().contains(cap.getNode())) {
                root.getChildren().add(cap.getNode());
                }
                cap.setVisible(true);
            }
        }
                    }
                }

                if (ball != null) ball.render();
                if (paddle != null) paddle.render();
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak()) brick.render();
                }

                // Update score display
                scoreText.setText("Score: " + score);

                if (ball.getY() > heightW) {
                    Update.loseLifeSound.play(SoundManager.getEffectVolume());
                    loseLife();
                }
            }
        };
        gameLoop.start();
    }

    private void applyEffect(Capsule capsule) {
        if (capsule != null) {
            capsule.playSound();
        }
        String type = capsule.getEffectType();
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
        else if (type.equals("fireBall")) {
            EffectManager.activateFireBall(ball);
        }
        else if (type.equals("powerBall")) {
            EffectManager.updatePower(ball, 3.0);
        }
        else if (type.equals("expandPaddle")) {
            EffectManager.changeWidth(paddle, 2.0);
        }
        else if (type.equals("shrinkPaddle")) {
            EffectManager.changeWidth(paddle, 0.5);
        }
        else if (type.equals("explosion")) {
            showExplosion(capsule.getX(), capsule.getY());
            loseLife();
        }

        highestScore = Math.max(score, highestScore);
        if (capsule != null) {
            capsule.playSound();
        }
    }

    private void showExplosion(double x, double y) {
        Image explosionImage = new Image("file:resources/explosion.gif");
        ImageView explosionView = new ImageView(explosionImage);

        explosionView.setFitWidth(100);
        explosionView.setFitHeight(100);
        explosionView.setX(x);
        explosionView.setY(y);
        root.getChildren().add(explosionView);

        // Remove after animation duration, assume 2 seconds for the GIF
        PauseTransition removeDelay = new PauseTransition(Duration.seconds(2));
        removeDelay.setOnFinished(event -> root.getChildren().remove(explosionView));
        removeDelay.play();
    }

    private void setPaddleDefault() {
        paddle.setWidth(widthP);
        paddle.setHeight(heightP);
    }

    private void setBallDefault() {
        double centerX = paddle.getX() + widthP / 2;
        double centerY = paddle.getY() - radiusB;
        ball.setDx(0);
        ball.setDy(0);
        ball.setX(centerX);
        ball.setY(centerY);
        ball.setSpeed(speedB);
        ball.setPower(1);
        ball.setFireBall(false);
    }

    private void loseLife() {
        lives--;
        if (lives > 0) {
            // Remove one heart
            if (!heartImages.isEmpty()) {
                ImageView lastHeart = heartImages.remove(heartImages.size() - 1);
                root.getChildren().remove(lastHeart);
            }
            // Reset ball and paddle
            setPaddleDefault();
            setBallDefault();
            isAttached = true;
        } else {
            gameLoop.stop();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            saveHighestScore();
            Platform.runLater(() -> {
                primaryStage.close();
                EndMenu.show(score, highestScore);
            });
        }
    }

    private static void saveHighestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Path.highestScore))) {
            writer.write(String.valueOf(highestScore));
            System.out.println("Đã lưu highestScore: " + highestScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createAndShowGame() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainGame game = new MainGame();
            game.start(stage);
        });
    }

    public static int getBestScore(){
        return highestScore;
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Path.highestScore))) {
            String line = reader.readLine();
            if (line != null) highestScore = Integer.parseInt(line.trim());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        GameMenu.showMenu();
    }
}