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

/**
 * Lớp chính điều khiển toàn bộ trò chơi Arkanoid.
 * Quản lý bóng, paddle, gạch, capsule, điểm số, mạng, level, âm thanh, pause...
 */
public class MainGame {
    // Cài đặt kích thước màn hình và đối tượng
    private final int widthW = 1080;
    private final int heightW = 720;
    private final int widthP = 150;
    private final int heightP = 30;
    private final int radiusB = 15;
    private int speedB = 7;                    // Tốc độ bóng, tăng dần theo level
    private final int speedC = 2;
    private final int wallThickness = 30;

    private int numberBrokeBrick = 9;          // Số gạch đã phá trong level hiện tại
    private int numberLevel = 1;               // Level hiện tại

    // Các đối tượng trong game
    private Ball ball;
    private Paddle paddle;
    private Wall leftWall, rightWall, topWall;
    private Bricks[] bricks;
    private Capsule[] capsules;
    private List<Integer> capsuleIndex = new ArrayList<>();

    private Group root;
    private AnimationTimer gameLoop;
    private Stage primaryStage;

    // Điểm số, mạng, giao diện
    private int score = 0;
    private static int highestScore;
    private MediaPlayer mediaPlayer;
    private boolean isAttached = true;         // Bóng dính vào paddle
    private int lives = 5;
    private List<ImageView> heartImages = new ArrayList<>();
    private Text scoreText;
    private Text levelText;
    private Image heartImage;
    private Image collisionImage;

    // Trạng thái pause và paddle tĩnh để Pause truy cập
    public static boolean isPaused = false;
    public static Paddle staticPaddle;

    // Tạo gạch và capsule (gọi lại khi qua level)
    private void genBrickAndCapsule() {
        int[] hardnessArray = {1, 2, 3, 4};
        Random random = new Random();
        int brickWidth = 90;
        int brickHeight = 30;
        int spacing = 5;
        int rowCount = Math.min(numberLevel, 5);
        int colCount = 10;

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                double brickX = col * (brickWidth + spacing) + wallThickness + 30;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;

                int randomHardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomHardness);

                double chance = random.nextDouble();
                if (chance < 0.3) {
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                } else {
                    chance = random.nextDouble();
                    if (chance < 0.2) {
                        capsules[index] = new Capsule(Path.explosionCapsule, Path.explosionSound);
                        capsules[index].init(brickX, brickY, brickWidth, brickHeight, speedC, "explosion");
                        capsules[index].setVisible(false);
                        capsuleIndex.add(index);
                    } else {
                        capsules[index] = null;
                    }
                }
            }
        }
    }

    // Constructor: Khởi tạo toàn bộ game
    public MainGame() {
        collisionImage = new Image("file:resources/boom_collision.gif");

        double paddleX = (widthW - widthP) / 2.0;
        double paddleY = heightW - heightP;
        paddle = new Paddle(paddleX, paddleY, widthP, heightP);
        MainGame.staticPaddle = paddle;

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
        genBrickAndCapsule();

        heartImage = new Image("file:resources/heart.png");
        for (int i = 0; i < lives; i++) {
            ImageView iv = new ImageView(heartImage);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setX(widthW - wallThickness - 80 - i * 36);
            iv.setY(wallThickness + 5);
            heartImages.add(iv);
        }

        scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(36));
        scoreText.setX(widthW - wallThickness - 200);
        scoreText.setY(wallThickness + 64);

        levelText = new Text("Level " + numberLevel);
        levelText.setFill(Color.WHITE);
        levelText.setFont(new Font(36));
        levelText.setX(wallThickness + 20);
        levelText.setY(wallThickness + 64);
    }

    // Hiệu ứng nổ khi bóng chạm gạch (chỉ khi không phải fireball)
    private void showBrickCollisionEffect(double x, double y) {
        ImageView effect = new ImageView(collisionImage);
        effect.setFitWidth(60);
        effect.setFitHeight(60);
        effect.setX(x - 30);
        effect.setY(y - 30);
        root.getChildren().add(effect);

        PauseTransition remove = new PauseTransition(Duration.seconds(1.0));
        remove.setOnFinished(e -> root.getChildren().remove(effect));
        remove.play();
    }

    // Khởi động game
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new Group();
        Scene scene = new Scene(root, widthW, heightW, Color.BLACK);

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            if (gameLoop != null) gameLoop.stop();
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

    // Phát nhạc nền
    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.backgroundMusic.substring(1));
            Media media = soundURL != null
                ? new Media(soundURL.toString())
                : new Media(Path.getFileURL(Path.backgroundMusic));

            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Cannot find BackgroundMusic.wav at " + Path.backgroundMusic);
            e.printStackTrace();
        }
    }

    // Thêm tất cả đối tượng vào màn hình
    private void addGameElementsToRoot() {
        if (paddle != null && paddle.getNode() != null) root.getChildren().add(paddle.getNode());
        if (ball != null && ball.getNode() != null) root.getChildren().add(ball.getNode());
        if (leftWall != null && leftWall.getNode() != null) root.getChildren().add(leftWall.getNode());
        if (rightWall != null && rightWall.getNode() != null) root.getChildren().add(rightWall.getNode());
        if (topWall != null && topWall.getNode() != null) root.getChildren().add(topWall.getNode());

        if (bricks != null) {
            for (Bricks brick : bricks) {
                if (brick != null && brick.getNode() != null) {
                    root.getChildren().add(brick.getNode());
                }
            }
        }

        root.getChildren().add(scoreText);
        root.getChildren().add(levelText);
        for (ImageView heart : heartImages) root.getChildren().add(heart);
    }

    // Xử lý input: chuột, phím
    private void setupInput(Scene scene) {
        scene.setOnMouseMoved(event -> {
            if (isPaused) return;
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
                isPaused = true;
                Pause.show(primaryStage, gameLoop);
            }
        });
    }

    // Vòng lặp game chính
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cập nhật capsule
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

                // Bóng dính paddle
                if (isAttached) {
                    double centerX = paddle.getX() + widthP / 2;
                    double centerY = paddle.getY() - radiusB;
                    ball.setX(centerX);
                    ball.setY(centerY);
                    ball.setDx(0);
                    ball.setDy(0);
                } else {
                    double ballSpeed = ball.getSpeed();
                    int subSteps = Math.max(1, (int) Math.ceil(ballSpeed / 5.0));

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
                            numberBrokeBrick++;

                            Bricks brokenBrick = bricks[breakIndex];
                            double brickCenterX = brokenBrick.getX() + brokenBrick.getWidth() / 2;
                            double brickCenterY = brokenBrick.getY() + brokenBrick.getHeight() / 2;

                            if (ball.getPower() > 0) {
                                showBrickCollisionEffect(brickCenterX, brickCenterY);
                            }

                            root.getChildren().remove(bricks[breakIndex].getNode());
                            bricks[breakIndex] = null;

                            if (capsules[breakIndex] != null && !capsules[breakIndex].isVisible()) {
                                Capsule cap = capsules[breakIndex];
                                if (lives == 5 && cap.getEffectType().equals("improveLife")) {
                                    if (root.getChildren().contains(cap.getNode())) {
                                        root.getChildren().remove(cap.getNode());
                                    }
                                    cap.setVisible(false);
                                    cap = null;
                                } else {
                                    if (!root.getChildren().contains(cap.getNode())) {
                                        root.getChildren().add(cap.getNode());
                                    }
                                    cap.setVisible(true);
                                }
                            }
                        }
                    }
                }

                // Render
                if (ball != null) ball.render();
                if (paddle != null) paddle.render();
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak()) brick.render();
                }

                scoreText.setText("Score: " + score);

                // Mất mạng
                if (ball.getY() > heightW) {
                    Update.loseLifeSound.play(SoundManager.getEffectVolume());
                    loseLife();
                }

                // Qua level
                if (numberBrokeBrick == 10 * Math.min(5, numberLevel)) {
                    speedB += 5;
                    ball.setSpeed(speedB);
                    numberLevel++;
                    levelText.setText("Level: " + numberLevel);
                    numberBrokeBrick = 0;

                    // Xóa gạch cũ
                    for (int i = 0; i < bricks.length; i++) {
                        if (bricks[i] != null && bricks[i].getNode() != null) {
                            root.getChildren().remove(bricks[i].getNode());
                            bricks[i] = null;
                        }
                    }

                    // Xóa capsule cũ
                    for (int i = 0; i < capsules.length; i++) {
                        if (capsules[i] != null && capsules[i].getNode() != null) {
                            root.getChildren().remove(capsules[i].getNode());
                            capsules[i] = null;
                        }
                    }

                    capsuleIndex.clear();
                    setPaddleDefault();
                    setBallDefault();
                    isAttached = true;
                    genBrickAndCapsule();

                    // Thêm gạch mới
                    for (Bricks brick : bricks) {
                        if (brick != null && brick.getNode() != null) {
                            root.getChildren().add(brick.getNode());
                        }
                    }
                }
            }
        };
        gameLoop.start();
    }

    // Áp dụng hiệu ứng capsule
    private void applyEffect(Capsule capsule) {
        if (capsule != null) capsule.playSound();
        String type = capsule.getEffectType();

        switch (type) {
            case "inc10Point": score += 10; break;
            case "dec10Point": score -= 10; break;
            case "inc50Point": score += 50; break;
            case "dec50Point": score -= 50; break;
            case "inc100Point": score += 100; break;
            case "dec100Point": score -= 100; break;
            case "fastBall": EffectManager.updateSpeed(ball, 1.5); break;
            case "slowBall": EffectManager.updateSpeed(ball, 0.5); break;
            case "fireBall": EffectManager.activateFireBall(ball); break;
            case "powerBall": EffectManager.updatePower(ball, 3.0); break;
            case "expandPaddle": EffectManager.changeWidth(paddle, 2.0); break;
            case "shrinkPaddle": EffectManager.changeWidth(paddle, 0.5); break;
            case "improveLife": addLife(); break;
            case "explosion":
                showExplosion(capsule.getX(), capsule.getY());
                loseLife();
                break;
        }

        highestScore = Math.max(score, highestScore);
        if (capsule != null) capsule.playSound();
    }

    // Hiệu ứng nổ lớn (capsule explosion)
    private void showExplosion(double x, double y) {
        Image explosionImage = new Image("file:resources/explosion.gif");
        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setFitWidth(100);
        explosionView.setFitHeight(100);
        explosionView.setX(x);
        explosionView.setY(y);
        root.getChildren().add(explosionView);

        PauseTransition removeDelay = new PauseTransition(Duration.seconds(2));
        removeDelay.setOnFinished(event -> root.getChildren().remove(explosionView));
        removeDelay.play();
    }

    // Reset paddle về mặc định
    private void setPaddleDefault() {
        paddle.setWidth(widthP);
        paddle.setHeight(heightP);
    }

    // Reset bóng về vị trí ban đầu
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

    // Tăng 1 mạng
    private void addLife() {
        ImageView lastHeart = new ImageView(heartImage);
        lastHeart.setFitWidth(30);
        lastHeart.setFitHeight(30);
        lastHeart.setX(widthW - wallThickness - 80 - lives * 36);
        lastHeart.setY(wallThickness + 5);
        root.getChildren().add(lastHeart);
        heartImages.add(lastHeart);
        lives++;
    }

    // Mất 1 mạng
    private void loseLife() {
        lives--;
        if (lives > 0) {
            if (!heartImages.isEmpty()) {
                ImageView lastHeart = heartImages.remove(heartImages.size() - 1);
                root.getChildren().remove(lastHeart);
            }
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

    // Lưu điểm cao nhất
    private static void saveHighestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Path.highestScore))) {
            writer.write(String.valueOf(highestScore));
            System.out.println("Đã lưu highestScore: " + highestScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tạo và hiển thị game từ menu
    public static void createAndShowGame() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainGame game = new MainGame();
            game.start(stage);
        });
    }

    public static int getBestScore() {
        return highestScore;
    }

    // Main: Đọc điểm cao nhất và mở menu
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Path.highestScore))) {
            String line = reader.readLine();
            if (line != null) highestScore = Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        GameMenu.showMenu();
    }
}