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
 * HIỆU ỨNG ĐUÔI BÓNG: Dùng class riêng BallTrailEffect để quản lý.
 */
public class MainGame {
    // Cài đặt kích thước màn hình và đối tượng
    private final int widthW = 1080;
    private final int heightW = 720;
    private final int widthP = 150;
    private final int heightP = 30;
    private final int radiusB = 15;
    private int speedB = 5;                    // Tốc độ bóng, tăng dần theo level
    private final int speedC = 2;
    private final int wallThickness = 30;
    private int numberLevel = 1;               // Level hiện tại

    // Các đối tượng trong game
    private Ball ball;
    private Paddle paddle;
    private Wall leftWall, rightWall, topWall;
    private Bricks[] bricks;
    private Capsule[] capsules;
    private List<Integer> capsuleIndex = new ArrayList<>();
    private Group root;
    private static AnimationTimer gameLoop;
    private Stage primaryStage;
    // Điểm số, mạng, giao diện
    private int score = 0;
    private static int highestScore;
    private static MediaPlayer mediaPlayer;
    private boolean isAttached = true;         // Bóng dính vào paddle
    private int lives = 10;
    private List<ImageView> heartImages = new ArrayList<>();
    private Text scoreText;
    private Text levelText;
    private Image heartImage;
    private Image collisionImage;
    private Image fireCollisionImage;
    private GameLevel gameLevel;
    // Trạng thái pause và paddle tĩnh để Pause truy cập
    public static boolean isPaused = false;
    public static Paddle staticPaddle;

    private BallTrailEffect ballTrailEffect;
    
    public static void cleanup() {
        System.out.println("MainGame.cleanup() started.");

        // Dừng game loop
        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("GameLoop stopped in cleanup.");
        }

        // Dừng nhạc nền
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            VolumeManager.unregisterMediaPlayer(mediaPlayer);
            System.out.println("MediaPlayer stopped.");
        }

        // Dừng tất cả âm thanh hiệu ứng
        VolumeManager.stopAllSounds();
        System.out.println("All sounds stopped in cleanup.");

        // Lưu điểm cao nhất
        saveHighestScore();
        System.out.println("Highest score saved. Cleanup completed.");
    }

    private void genBrickAndCapsule() {
        try {
            System.out.println("genBrickAndCapsule() called. numberLevel=" + numberLevel);
            if (gameLevel == null) {
                System.err.println("ERROR: gameLevel is null in genBrickAndCapsule!");
                return;
            }

            gameLevel.nextLevel();

            // Lấy level hiện tại
            Level currentLevel = gameLevel.getCurrentLevel();
            if (currentLevel == null) {
                System.err.println("ERROR: gameLevel.getCurrentLevel() returned null");
                return;
            }

            this.bricks = currentLevel.getBricks();
            this.capsules = currentLevel.getCapsules();
            this.capsuleIndex = currentLevel.getCapsuleIndex();

            System.out.println("Loaded currentLevel. bricks=" + (bricks == null ? "null" : String.valueOf(bricks.length))
                    + ", capsules=" + (capsules == null ? "null" : String.valueOf(capsules.length))
                    + ", capsuleIndex=" + (capsuleIndex == null ? "null" : String.valueOf(capsuleIndex.size())));

            // Thêm từng viên gạch vào scene (guard null)
            if (bricks != null) {
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak() && brick.getNode() != null) {
                        if (!root.getChildren().contains(brick.getNode())) {
                            root.getChildren().add(brick.getNode());
                        }
                    }
                }
            } else {
                System.err.println("Warning: bricks array is null for this level.");
            }
        }
        catch (Exception e) {
            System.err.println("Error in generating level.");
            e.printStackTrace();
        }
    }

    private boolean isLevelCleared() {
        if (bricks == null) {
            // if bricks null, treat as cleared (or decide to treat as not cleared based on your logic)
            System.err.println("isLevelCleared(): bricks array is null -> treating as cleared");
            return true;
        }
        for (Bricks brick : bricks) {
            if (brick != null && !brick.isBreak()) {
                return false;
            }
        }
        return true;
    }


    // Constructor: Khởi tạo toàn bộ game
    public MainGame() {
        gameLevel = new GameLevel(5, wallThickness, speedC);
        collisionImage = new Image("file:resources/boom_collision.gif");
        fireCollisionImage = new Image("file:resources/fire_collision.gif");

        // Tạo paddle
        double paddleX = (widthW - widthP) / 2.0;
        double paddleY = heightW - heightP;
        paddle = new Paddle(paddleX, paddleY, widthP, heightP);
        MainGame.staticPaddle = paddle;

        // Tạo bóng (dính vào paddle)
        double centerX = paddleX + widthP / 2;
        double centerY = paddleY - radiusB;
        ball = new Ball(centerX, centerY, radiusB, speedB);
        ball.setDx(0);
        ball.setDy(0);

        // Tạo tường
        leftWall = new Wall("left", 0, 0, wallThickness, heightW, wallThickness);
        rightWall = new Wall("right", widthW - wallThickness, 0, wallThickness, heightW, wallThickness);
        topWall = new Wall("top", 0, 0, widthW, wallThickness, wallThickness);

        genBrickAndCapsule();

        // Tạo trái tim (mạng)
        heartImage = new Image("file:resources/heart.png");
        for (int i = 0; i < lives; i++) {
            ImageView iv = new ImageView(heartImage);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setX(widthW - wallThickness - 80 - i * 36);
            iv.setY(wallThickness + 5);
            heartImages.add(iv);
        }

        // Text điểm số
        scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(36));
        scoreText.setX(widthW - wallThickness - 200);
        scoreText.setY(wallThickness + 64);

        // Text level
        levelText = new Text("Level " + numberLevel);
        levelText.setFill(Color.WHITE);
        levelText.setFont(new Font(36));
        levelText.setX(wallThickness + 20);
        levelText.setY(wallThickness + 64);
    }

    private void showBrickCollisionEffect(double x, double y) {
        boolean isFire = ball.isFireBall();
        Image effectImage = isFire ? fireCollisionImage : collisionImage;

        ImageView effect = new ImageView(effectImage);
        effect.setFitWidth(60);
        effect.setFitHeight(60);
        effect.setX(x - 30);
        effect.setY(y - 30);
        root.getChildren().add(effect);

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
            System.out.println("Window close requested - calling cleanup...");
            cleanup();
        });

        primaryStage.show();

        // Delay 3 giây trước khi bắt đầu
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            addGameElementsToRoot();   // Thêm đối tượng + khởi tạo trail
            setupInput(scene);         // Xử lý input
            startGameLoop();           // Bắt đầu vòng lặp
            playBackgroundMusic();     // Phát nhạc
        });
        delay.play();
    }

    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.backgroundMusic.substring(1));
            Media media = soundURL != null
                    ? new Media(soundURL.toString())
                    : new Media(Path.getFileURL(Path.backgroundMusic));

            mediaPlayer = new MediaPlayer(media);
            VolumeManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Cannot find BackgroundMusic.wav at " + Path.backgroundMusic);
            e.printStackTrace();
        }
    }

    private void addGameElementsToRoot() {
        // Thêm paddle, bóng, tường
        if (paddle != null && paddle.getNode() != null) root.getChildren().add(paddle.getNode());
        if (ball != null && ball.getNode() != null) root.getChildren().add(ball.getNode());
        if (leftWall != null && leftWall.getNode() != null) root.getChildren().add(leftWall.getNode());
        if (rightWall != null && rightWall.getNode() != null) root.getChildren().add(rightWall.getNode());
        if (topWall != null && topWall.getNode() != null) root.getChildren().add(topWall.getNode());

        // Thêm gạch
        if (bricks != null) {
            for (Bricks brick : bricks) {
                if (brick != null && brick.getNode() != null) {
                    root.getChildren().add(brick.getNode());
                }
            }
        }

        // Thêm UI
        root.getChildren().add(scoreText);
        root.getChildren().add(levelText);
        
        for (ImageView heart : heartImages) root.getChildren().add(heart);
            // KHỞI TẠO HIỆU ỨNG ĐUÔI BÓNG (sau khi ball đã vào scene)
            ballTrailEffect = new BallTrailEffect(ball.getNode(), root, 20);
        }

    private void setupInput(Scene scene) {
        // Di chuyển paddle bằng chuột
        scene.setOnMouseMoved(event -> {
            if (isPaused) return;
            if (paddle != null && leftWall != null && rightWall != null) {
                paddle.move(event, leftWall, rightWall);
            }
        });

        // Bấm chuột để thả bóng
        scene.setOnMouseClicked(event -> {
            if (isAttached) {
                isAttached = false;
                ball.setDy(-speedB);
            }
        });

        // Nhấn ESC để pause
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                isPaused = true;
                Pause.show(primaryStage, gameLoop);
            }
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // === CẬP NHẬT CAPSULE ===
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

                // === BÓNG DÍNH PADDLE ===
                if (isAttached) {
                    double centerX = paddle.getX() + widthP / 2;
                    double centerY = paddle.getY() - radiusB;
                    ball.setX(centerX);
                    ball.setY(centerY);
                    ball.setDx(0);
                    ball.setDy(0);
                } else {
                    // === CẬP NHẬT VỊ TRÍ BÓNG (với sub-steps) ===
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

                            Bricks brokenBrick = bricks[breakIndex];
                            double brickCenterX = brokenBrick.getX() + brokenBrick.getWidth() / 2;
                            double brickCenterY = brokenBrick.getY() + brokenBrick.getHeight() / 2;

                            if (ball.getPower() >= 1) {
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

                    // THÊM DƯ ẢNH BÓNG (dùng class riêng)
                    if (ball.getDx() != 0 || ball.getDy() != 0) {
                        ballTrailEffect.update(ball.getX(), ball.getY());
                    }
                }

                // === RENDER ===
                if (ball != null) ball.render();
                if (paddle != null) paddle.render();
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak()) brick.render();
                }

                scoreText.setText("Score: " + score);

                // === MẤT MẠNG ===
                if (ball.getY() > heightW) {
                    Update.loseLifeSound.play(VolumeManager.getEffectVolume());
                    loseLife();
                }
                // Qua level
                if (isLevelCleared()) {
                    numberLevel++;
                    levelText.setText("Level: " + numberLevel);

                    // Xóa brick cũ
                    for(Bricks brick : bricks) {
                        if (brick != null && brick.getNode() != null) {
                            root.getChildren().remove(brick.getNode());
                        }
                    }

                    // Xóa capsule cũ
                    for(Capsule capsule : capsules) {
                        if (capsule != null && capsule.getNode() != null) {
                            root.getChildren().remove(capsule.getNode());
                        }
                    }
                    capsuleIndex.clear();

                    // Reset paddle & bóng
                   genBrickAndCapsule();
                    setPaddleDefault();
                    setBallDefault();
                    isAttached = true;

                    // Xóa dư ảnh cũ
                    if (ballTrailEffect != null) {
                        ballTrailEffect.clear();
                    }

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

    private void applyEffect(Capsule capsule) {
        if (capsule != null) capsule.playSound();
        String type = capsule.getEffectType();

        switch (type) {
            case "inc10Point":   score += 10; break;
            case "dec10Point":   score -= 10; break;
            case "inc50Point":   score += 50; break;
            case "dec50Point":   score -= 50; break;
            case "inc100Point":  score += 100; break;
            case "dec100Point":  score -= 100; break;
            case "fastBall":     EffectManager.updateSpeed(ball, 1.5); break;
            case "slowBall":     EffectManager.updateSpeed(ball, 0.5); break;
            case "fireBallCapsule": EffectManager.activateFireBall(ball); break;
            case "powerBall":    EffectManager.updatePower(ball, 3.0); break;
            case "expandPaddle": EffectManager.changeWidth(paddle, 2.0); break;
            case "shrinkPaddle": EffectManager.changeWidth(paddle, 0.5); break;
            case "healthCapsule":
                if (lives < 5) {
                    int newIndex = lives;
                    double newX = widthW - wallThickness - 80 - newIndex * 36;
                    ImageView newHeart = new ImageView(heartImage);
                    newHeart.setFitWidth(30);
                    newHeart.setFitHeight(30);
                    newHeart.setX(newX);
                    newHeart.setY(wallThickness + 5);
                    root.getChildren().add(newHeart);
                    heartImages.add(newHeart);
                    lives++;
                }
                break;
            case "explosion":
                showExplosion(capsule.getX(), capsule.getY());
                loseLife();
                break;
            default:
                System.out.println("Unknown capsule type: " + type);
                break;
        }

        highestScore = Math.max(score, highestScore);
    }

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

    private void setPaddleDefault() {
        paddle.setWidth(widthP);
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

        // Xóa dư ảnh khi reset bóng
        if (ballTrailEffect != null) {
            ballTrailEffect.clear();
        }
    }

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
                VolumeManager.unregisterMediaPlayer(mediaPlayer);
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

    public static int getBestScore() {
        return highestScore;
    }
    public static void main(String[] args) {
        // Đọc điểm cao nhất từ file
        try (BufferedReader reader = new BufferedReader(new FileReader(Path.highestScore))) {
            String line = reader.readLine();
            if (line != null) highestScore = Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        GameMenu.showMenu();
    }
}