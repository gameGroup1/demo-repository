package code_for_mainGame;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
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
import javafx.scene.input.KeyCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    private int speedB = 10; // Tốc độ bóng, tăng dần theo level
    private final int speedC = 2;
    private final int wallThickness = 30;
    private Ball ball;
    private Paddle paddle;
    private Wall leftWall, rightWall, topWall;
    private Bricks[] bricks;
    private Capsule[] capsules;
    private List<Integer> capsuleIndex = new ArrayList<>();
    private Group root = new Group();
    private static AnimationTimer gameLoop;
    private static Stage primaryStage;
    // Điểm số, mạng, giao diện
    private int score = 0;
    private static int bestLevel;
    private static int lastLevel;
   // private static int numberLevel = 1;
    private static MediaPlayer mediaPlayer; // dùng cho background music (giữ nguyên)
    // --- Thêm cho video background ---
    private static MediaPlayer bgVideoPlayer;
    private static MediaView bgMediaView;
    // -----------------------------------
    private boolean isAttached = true; // Bóng dính vào paddle
    private int lives = 10;
    private List<ImageView> heartImages = new ArrayList<>();
    private Text scoreText;
    private Text levelText;
    private Image heartImage;
    private Image collisionImage;
    private Image fireCollisionImage;
    private static GameLevel gameLevel;
    // Trạng thái pause và paddle tĩnh để Pause truy cập
    public static boolean isPaused = false;
    public static Paddle staticPaddle;
    private Scene scene;
    private boolean needToReset = false;
    private BallTrailEffect ballTrailEffect;

    // Get current level
    public static int getCurrentLevel() {
        return lastLevel;
    }

    // Constructor: Khởi tạo toàn bộ game
    public MainGame(int startLevel) {
        isPaused = false;
        lastLevel = startLevel;
        saveBestAndLastLevel();
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
        lives = 10;
        heartImage = new Image("file:resources/heart.png");
        for (int i = 0; i < lives; i++) {
            ImageView iv = new ImageView(heartImage);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setX(widthW - wallThickness - 80 - i * 36);
            iv.setY(wallThickness + 5);
            heartImages.add(iv);
        }
        score = 0;

        String glowShadowStyle = "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
                "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        // Score Text
        scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.rgb(255, 255, 255, 0.95));
        scoreText.setFont(new Font(36));
        scoreText.setStyle(glowShadowStyle);
        scoreText.setX(widthW - wallThickness - 200);
        scoreText.setY(wallThickness + 64);
        levelText = new Text("Level " + lastLevel);
        levelText.setFill(Color.rgb(255, 255, 255, 0.95)); // Màu xanh dương cho level để thể hiện tiến bộ
        levelText.setFont(new Font(36));
        levelText.setStyle(glowShadowStyle);
        levelText.setX(wallThickness + 20);
        levelText.setY(wallThickness + 64);
        isAttached = true;
    }

    public static void cleanup() {
        System.out.println("MainGame.cleanup() started.");
        // Dừng game loop
        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("GameLoop stopped in cleanup.");
        }
        // Dừng âm thanh game (music)
        // Dừng nhạc nền
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            VolumeManager.unregisterMediaPlayer(mediaPlayer);
            System.out.println("MediaPlayer (music) stopped.");
        }
        // Dừng video background nếu có
        if (bgVideoPlayer != null) {
            bgVideoPlayer.stop();
            bgVideoPlayer.dispose();
            System.out.println("bgVideoPlayer stopped and disposed in cleanup.");
        }
        // Dừng tất cả âm thanh hiệu ứng
        VolumeManager.stopAllSounds();
        System.out.println("All sounds stopped in cleanup.");
        System.out.println("Best and last level saved. Cleanup completed.");
    }

    // Tạo gạch và capsule (gọi lại khi qua level)
    private void genBrickAndCapsule() {
        try {
            System.out.println("genBrickAndCapsule() called. numberLevel=" + lastLevel);
            if (gameLevel == null) {
                System.err.println("ERROR: gameLevel is null in genBrickAndCapsule!");
                return;
            }
            gameLevel.loadLevel(lastLevel);
            // Lấy level hiện tại
            Level currentLevel = gameLevel.getCurrentLevel();
            if (currentLevel == null) {
                System.err.println("ERROR: gameLevel.getCurrentLevel() returned null");
                return;
            }
            bricks = currentLevel.getBricks();
            capsules = currentLevel.getCapsules();
            capsuleIndex = currentLevel.getCapsuleIndex();
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
        scene = new Scene(root, widthW, heightW, Color.BLACK);
        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Window close requested - calling cleanup...");
            // Stop and dispose bgVideoPlayer (instance-level)
            try {
                if (bgVideoPlayer != null) {
                    bgVideoPlayer.stop();
                    bgVideoPlayer.dispose();
                    System.out.println("bgVideoPlayer stopped and disposed on close.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            cleanup();
        });

        primaryStage.show();
        playBackgroundVideo();
        addGameElementsToRoot();
        setupInput(scene);
        startGameLoop();
        playBackgroundMusic();
    }

    // Phát video background
    private void playBackgroundVideo() {
        try {
            // Nếu đã có player cũ, dừng và dispose trước khi (re)create
            if (bgVideoPlayer != null) {
                try {
                    bgVideoPlayer.stop();
                    bgVideoPlayer.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                bgVideoPlayer = null;
                bgMediaView = null;
            }

            // THỬ load từ classpath (khi đóng gói jar: put file under resources root)
            URL videoURL = getClass().getResource("/video_background.mp4"); // chú ý: resource path relative to resources root
            Media bgMedia;
            if (videoURL != null) {
                System.out.println("Background video URL: " + videoURL.toString());
                bgMedia = new Media(videoURL.toExternalForm());
            } else {
                // fallback: file hệ thống (chỉ dùng khi chạy từ IDE)
                File f = new File("resources/video_background.mp4");
                if (!f.exists()) {
                    System.err.println("Background file not found: " + f.getAbsolutePath());
                    return;
                }
                bgMedia = new Media(f.toURI().toString());
            }

            // Tạo player và đăng ký handler
            bgVideoPlayer = new MediaPlayer(bgMedia);

            bgVideoPlayer.setOnError(() -> {
                System.err.println("bgVideoPlayer onError: " + bgVideoPlayer.getError());
                if (bgVideoPlayer.getError() != null) bgVideoPlayer.getError().printStackTrace();
            });
            bgMedia.setOnError(() -> {
                System.err.println("Media onError: " + bgMedia.getError());
                if (bgMedia.getError() != null) bgMedia.getError().printStackTrace();
            });

            bgVideoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgVideoPlayer.setMute(true);

            // Chờ ready trước khi play để tránh freeze/seek bug
            bgVideoPlayer.setOnReady(() -> {
                System.out.println("Background video ready, duration: " + bgMedia.getDuration());
                // Tạo MediaView chỉ sau khi ready (an toàn hơn)
                bgMediaView = new MediaView(bgVideoPlayer);
                bgMediaView.setPreserveRatio(false);
                bgMediaView.setMouseTransparent(true);
                bgMediaView.fitWidthProperty().bind(scene.widthProperty());
                bgMediaView.fitHeightProperty().bind(scene.heightProperty());
                // Đặt phía sau: index 0
                if (!root.getChildren().contains(bgMediaView)) {
                    root.getChildren().add(0, bgMediaView);
                }
                bgVideoPlayer.play();
            });

        } catch (Exception e) {
            System.err.println("Không thể load background video:");
            e.printStackTrace();
        }
    }

    // Phát nhạc nền
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
                // === RESET ===
                if (lastLevel >= 2) reset();
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
                    needToReset = true;
                    lastLevel++;
                    levelText.setText("Level: " + lastLevel);
                    isPaused = true;
                    // Dừng game loop ngay (ngăn tiếp tục animation)
                    if (gameLoop != null) gameLoop.stop();

                    // Đưa việc hiển thị cửa sổ ra ngoài pulse hiện tại để tránh IllegalStateException
                    if (lastLevel < 8) Platform.runLater(() -> WinLevel.show(primaryStage, gameLoop));
                    else {
                        gameLoop.stop();
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            VolumeManager.unregisterMediaPlayer(mediaPlayer);
                        }
                        // Stop & dispose background video player (nếu có)
                        try {
                            if (bgVideoPlayer != null) {
                                bgVideoPlayer.stop();
                                bgVideoPlayer.dispose();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            primaryStage.close();
                            WinAllLevels.show(primaryStage, gameLoop);
                        });
                    }

                    // Xóa brick cũ
                    for (Bricks brick : bricks) {
                        if (brick != null && brick.getNode() != null) {
                            root.getChildren().remove(brick.getNode());
                        }
                    }

                    // Xóa capsule cũ
                    for (Capsule capsule : capsules) {
                        if (capsule != null && capsule.getNode() != null) {
                            root.getChildren().remove(capsule.getNode());
                        }
                    }

                    // Xóa bóng
                    if (root.getChildren().contains(ball.getNode()))
                        root.getChildren().remove(ball.getNode());
                    capsuleIndex.clear();

                    // Xóa dư ảnh cũ
                    if (ballTrailEffect != null) {
                        ballTrailEffect.clear();
                    }
                }
            }
        };
        gameLoop.start();
    }

    // Reset sau khi tiếp tục màn chơi mới
    public void reset() {
        // Nếu reset rồi thì thôi
        if (!needToReset) return;
        needToReset = false;
        speedB += 5;
        ball.setSpeed(speedB);
        levelText.setText("Level: " + lastLevel);
        setPaddleDefault();
        setBallDefault();
        isAttached = true;
        genBrickAndCapsule();
        saveBestAndLastLevel();
        // Hiện lại bóng
        if (!root.getChildren().contains(ball.getNode()))
            root.getChildren().add(ball.getNode());
        // Thêm gạch mới
        for (Bricks brick : bricks) {
            if (brick != null && brick.getNode() != null && !root.getChildren().contains(brick.getNode())) {
                root.getChildren().add(brick.getNode());
            }
        }
    }

    // Áp dụng hiệu ứng capsule
    private void applyEffect(Capsule capsule) {
        if (capsule != null) capsule.playSound();
        String type = capsule.getEffectType();
        if (type.equals("inc10Point")) score += 10;
        else if (type.equals("dec10Point")) score -= 10;
        else if (type.equals("inc50Point")) score += 50;
        else if (type.equals("dec50Point")) score -= 50;
        else if (type.equals("inc100Point")) score += 100;
        else if (type.equals("dec100Point")) score -= 100;
        else if (type.equals("fastBall")) EffectManager.updateSpeed(ball, 1.5);
        else if (type.equals("slowBall")) EffectManager.updateSpeed(ball, 0.5);
        else if (type.equals("fireBall")) EffectManager.activateFireBall(ball);
        else if (type.equals("powerBall")) EffectManager.updatePower(ball, 3.0);
        else if (type.equals("expandPaddle")) EffectManager.changeWidth(paddle, 2.0);
        else if (type.equals("shrinkPaddle")) EffectManager.changeWidth(paddle, 0.5);
        else if (type.equals("healthCapsule")) {
            if (lives < 10) {
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
                System.out.println("Health capsule collected! Lives: " + lives);
            }
        } else if (type.equals("explosion")) {
            showExplosion(capsule.getX(), capsule.getY());
            loseLife();
        }
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
    }

    // Reset bóng về vị trí ban đầu
    private void setBallDefault() {
        double centerX = paddle.getX() + widthP / 2;
        double centerY = paddle.getY() - radiusB;
        isAttached = true;
        ball.setDx(0);
        ball.setDy(0);
        ball.setX(centerX);
        ball.setY(centerY);
        ball.setSpeed(speedB);
        ball.setPower(10);
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
            // Stop & dispose background video player (nếu có)
            try {
                if (bgVideoPlayer != null) {
                    bgVideoPlayer.stop();
                    bgVideoPlayer.dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Platform.runLater(() -> {
                primaryStage.close();
                EndMenu.show(score, lastLevel);
            });
        }
    }

    private static void saveBestAndLastLevel() {
        bestLevel = Math.max(bestLevel, lastLevel);
        try (BufferedWriter bestWriter = new BufferedWriter(new FileWriter(Path.bestLevel));
             BufferedWriter lastWriter = new BufferedWriter(new FileWriter(Path.lastLevel))) {
            bestWriter.write(String.valueOf(bestLevel));
            lastWriter.write(String.valueOf(lastLevel));
            System.out.println("Đã lưu bestLevel: " + bestLevel + ", lastLevel: " + lastLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createAndShowGame(int startLevel) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainGame game = new MainGame(startLevel);
            game.start(stage);
        });
    }

    public static int getBestLevel() {
        return bestLevel;
    }

    public static int getLastLevel() {
        return lastLevel;
    }

    public static void exitMainGame() {
        cleanup();
        System.exit(0);
    }

    public static void main(String[] args) {
        // Đọc level cao nhất và level cuối từ file
        try (BufferedReader bestReader = new BufferedReader(new FileReader(Path.bestLevel));
             BufferedReader lastReader = new BufferedReader(new FileReader(Path.lastLevel))) {
            String bestLine = bestReader.readLine();
            String lastLine = lastReader.readLine();
            if (bestLine != null) bestLevel = Integer.parseInt(bestLine.trim());
            if (lastLine != null) lastLevel = Integer.parseInt(lastLine.trim());
            if (lastLevel < 1) lastLevel = 1;
            System.out.println("Đã đọc bestLevel: " + bestLevel + ", lastLevel: " + lastLevel);
        } catch (IOException | NumberFormatException e) {
            bestLevel = 1;
            lastLevel = 1;
            e.printStackTrace();
        }
        GameMenu.showMenu();
    }
}