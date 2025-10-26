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
    // Kích thước cửa sổ game
    private final int widthW = 1080;
    private final int heightW = 720;

    // Kích thước thanh chắn (paddle)
    private final int widthP = 150;
    private final int heightP = 30;

    // Bán kính bóng
    private final int radiusB = 15;

    // Tốc độ di chuyển của bóng (có thể tăng theo level)
    private int speedB = 5;

    // Tốc độ rơi của capsule
    private final int speedC = 2;

    // Độ dày tường
    private final int wallThickness = 30;

    // Biến đếm số gạch bị phá
    private int numberBrokeBrick = 0;

    // Level hiện tại
    private int numberLevel = 1;

    // Các đối tượng game
    private Ball ball;
    private Paddle paddle;
    private Wall leftWall;
    private Wall rightWall;
    private Wall topWall;
    private Bricks[] bricks;
    private Capsule[] capsules;
    private List<Integer> capsuleIndex = new ArrayList<>(); // Danh sách chỉ số capsule đang tồn tại
    private Group root;
    private AnimationTimer gameLoop;
    private Stage primaryStage;

    // Điểm số và điểm cao nhất
    private int score = 0;
    private static int highestScore;

    // Âm thanh nền
    private MediaPlayer mediaPlayer;

    // Trạng thái bóng dính vào thanh chắn
    private boolean isAttached = true;

    // Số mạng (máu)
    private int lives = 5;
    private List<ImageView> heartImages = new ArrayList<>();

    // Hiển thị điểm và level
    private Text scoreText;
    private Text levelText;

    // Hình ảnh trái tim và hiệu ứng va chạm
    private Image heartImage;
    private Image collisionImage;

    // Tạo gạch và capsule ngẫu nhiên cho màn chơi
    public void genBrickAndCapsule() {
        int[] hardnessArray = {1, 2, 3, 4}; // Độ bền gạch: 1-4
        Random random = new Random();
        int brickWidth = 90;
        int brickHeight = 30;
        int spacing = 5;
        int rowCount = 5;
        int colCount = 10;

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // Tính vị trí gạch
                double brickX = col * (brickWidth + spacing) + wallThickness + 30;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;

                // Gán độ bền ngẫu nhiên
                int randomHardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, randomHardness);

                // Xác suất rơi capsule (30% có capsule, 20% có explosion)
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

    // Khởi tạo game: paddle, ball, tường, gạch, capsule, UI
    public MainGame() {
        int[] hardnessArray = {1, 2, 3, 4};
        Random random = new Random();

        // Hiệu ứng va chạm gạch
        collisionImage = new Image("file:resources/boom_collision.gif");

        // Vị trí thanh chắn ở giữa đáy
        double paddleX = (widthW - widthP) / 2.0;
        double paddleY = heightW - heightP;
        paddle = new Paddle(paddleX, paddleY, widthP, heightP);

        // Bóng xuất hiện ở giữa thanh chắn
        double centerX = paddleX + widthP / 2;
        double centerY = paddleY - radiusB;
        ball = new Ball(centerX, centerY, radiusB, speedB);
        ball.setDx(0);
        ball.setDy(0);

        // Tạo 3 bức tường (trái, phải, trên)
        leftWall = new Wall("left", 0, 0, wallThickness, heightW, wallThickness);
        rightWall = new Wall("right", widthW - wallThickness, 0, wallThickness, heightW, wallThickness);
        topWall = new Wall("top", 0, 0, widthW, wallThickness, wallThickness);

        // Khởi tạo mảng gạch và capsule (50 phần tử)
        bricks = new Bricks[50];
        capsules = new Capsule[50];
        genBrickAndCapsule(); // Tạo gạch và capsule cho level đầu

        // Tải hình ảnh trái tim
        heartImage = new Image("file:resources/heart.png");

        // Hiển thị số mạng bằng hình trái tim
        for (int i = 0; i < lives; i++) {
            ImageView iv = new ImageView(heartImage);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setX(widthW - wallThickness - 80 - i * 36);
            iv.setY(wallThickness + 5);
            heartImages.add(iv);
        }

        // Hiển thị điểm số
        scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(36));
        scoreText.setX(widthW - wallThickness - 200);
        scoreText.setY(wallThickness + 64);

        // Hiển thị level
        levelText = new Text("Level " + numberLevel);
        levelText.setFill(Color.WHITE);
        levelText.setFont(new Font(36));
        levelText.setX(wallThickness + 20);
        levelText.setY(wallThickness + 64);
    }

    // Hiệu ứng va chạm gạch (chỉ hiện khi không phải fireball)
    private void showBrickCollisionEffect(double x, double y) {
        ImageView effect = new ImageView(collisionImage);
        effect.setFitWidth(60);
        effect.setFitHeight(60);
        effect.setX(x - 30);  // Căn giữa
        effect.setY(y - 30);
        root.getChildren().add(effect);

        // Xóa hiệu ứng sau 1 giây
        PauseTransition remove = new PauseTransition(Duration.seconds(1.0));
        remove.setOnFinished(e -> root.getChildren().remove(effect));
        remove.play();
    }

    // Khởi động game: tạo scene, thêm delay 3s, sau đó bắt đầu
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new Group();
        Scene scene = new Scene(root, widthW, heightW, Color.BLACK);

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Dừng game và lưu điểm khi đóng cửa sổ
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

        // Delay 3 giây trước khi bắt đầu (cho menu hoặc hiệu ứng)
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            addGameElementsToRoot(); // Thêm các đối tượng vào màn hình
            setupInput(scene);       // Bắt sự kiện chuột/phím
            startGameLoop();         // Bắt đầu vòng lặp game
            playBackgroundMusic();   // Phát nhạc nền
        });
        delay.play();
    }

    // Phát nhạc nền (lặp vô hạn)
    private void playBackgroundMusic() {
        try {
            URL soundURL = getClass().getClassLoader().getResource(Path.backgroundMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("Phát nhạc nền từ classpath");
            } else {
                media = new Media(Path.getFileURL(Path.backgroundMusic));
                System.out.println("Phát nhạc nền từ: " + Path.backgroundMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không tìm thấy file nhạc nền: " + Path.backgroundMusic);
            e.printStackTrace();
        }
    }

    // Thêm tất cả đối tượng game vào root (màn hình)
    private void addGameElementsToRoot() {
        if (paddle != null && paddle.getNode() != null) {
            root.getChildren().add(paddle.getNode());
        }
        if (ball != null && ball.getNode() != null) {
            root.getChildren().add(ball.getNode());
        }

        // Thêm tường
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

        // Thêm UI: điểm, level, mạng
        root.getChildren().add(scoreText);
        root.getChildren().add(levelText);
        for (ImageView heart : heartImages) {
            root.getChildren().add(heart);
        }
    }

    // Xử lý input: di chuyển paddle bằng chuột, click để thả bóng, ESC để tạm dừng
    private void setupInput(Scene scene) {
        scene.setOnMouseMoved(event -> {
            if (paddle != null && leftWall != null && rightWall != null) {
                paddle.move(event, leftWall, rightWall); // Di chuyển paddle theo chuột
            }
        });
        scene.setOnMouseClicked(event -> {
            if (isAttached) {
                isAttached = false;
                ball.setDy(-speedB); // Thả bóng khi click
            }
        });
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Pause.show(primaryStage, gameLoop); // Mở menu tạm dừng
            }
        });
    }

    // Vòng lặp game chính (AnimationTimer)
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cập nhật vị trí capsule (nếu có)
                for (int index : capsuleIndex) {
                    Capsule cap = capsules[index];
                    if (cap == null || !cap.isVisible()) continue;
                    Update.position(cap);
                    if (cap.getY() + cap.getHeight() > heightW) {
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                    }
                    if (Collision.check(paddle, cap)) {
                        applyEffect(cap); // Áp dụng hiệu ứng khi paddle chạm capsule
                        cap.setVisible(false);
                        root.getChildren().remove(cap.getNode());
                    }
                    cap.render();
                }

                // Bóng dính vào paddle khi chưa thả
                if (isAttached) {
                    double centerX = paddle.getX() + widthP / 2;
                    double centerY = paddle.getY() - radiusB;
                    ball.setX(centerX);
                    ball.setY(centerY);
                    ball.setDx(0);
                    ball.setDy(0);
                } else {
                    // Cập nhật vị trí bóng (với sub-step để tránh xuyên vật thể)
                    double ballSpeed = ball.getSpeed();
                    int subSteps = (int) Math.ceil(ballSpeed / 5.0);
                    if (subSteps < 1) subSteps = 1;

                    for (int s = 0; s < subSteps; s++) {
                        double stepDx = ball.getDx() / subSteps;
                        double stepDy = ball.getDy() / subSteps;

                        ball.setX(ball.getX() + stepDx);
                        ball.setY(ball.getY() + stepDy);

                        // Va chạm với tường và paddle
                        Update.position(ball, leftWall);
                        Update.position(ball, rightWall);
                        Update.position(ball, topWall);
                        Update.position(ball, paddle);

                        // Va chạm với gạch
                        int breakIndex = Update.position(ball, bricks);
                        if (breakIndex != -1 && bricks[breakIndex].isBreak()) {
                            score += 10;
                            highestScore = Math.max(score, highestScore);
                            numberBrokeBrick++;

                            // Hiệu ứng va chạm gạch (trừ khi là fireball)
                            Bricks brokenBrick = bricks[breakIndex];
                            double brickCenterX = brokenBrick.getX() + brokenBrick.getWidth() / 2;
                            double brickCenterY = brokenBrick.getY() + brokenBrick.getHeight() / 2;

                            if (ball.getPower() == 1) {
                                showBrickCollisionEffect(brickCenterX, brickCenterY);
                            }

                            // Xóa gạch khỏi màn hình
                            root.getChildren().remove(bricks[breakIndex].getNode());
                            bricks[breakIndex] = null;

                            // Rơi capsule nếu có
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

                // Vẽ lại các đối tượng
                if (ball != null) ball.render();
                if (paddle != null) paddle.render();
                for (Bricks brick : bricks) {
                    if (brick != null && !brick.isBreak()) brick.render();
                }

                // Cập nhật điểm số trên màn hình
                scoreText.setText("Score: " + score);

                // Mất mạng nếu bóng rơi xuống đáy
                if (ball.getY() > heightW) {
                    Update.loseLifeSound.play(SoundManager.getEffectVolume());
                    loseLife();
                }

                // Chuyển level khi phá hết 50 gạch
                if (numberBrokeBrick == 50) {
                    speedB += 5;
                    ball.setSpeed(speedB);
                    numberLevel++;
                    levelText.setText("Level: " + numberLevel);
                    numberBrokeBrick = 0;

                    // Xóa toàn bộ gạch và capsule cũ
                    for (Bricks brick : bricks) {
                        if (brick != null && brick.getNode() != null) {
                            root.getChildren().remove(brick.getNode());
                            brick = null;
                        }
                    }
                    for (Capsule capsule : capsules) {
                        if (capsule != null && capsule.getNode() != null) {
                            root.getChildren().remove(capsule.getNode());
                            capsule = null;
                        }
                    }

                    // Reset paddle và bóng
                    setPaddleDefault();
                    setBallDefault();
                    isAttached = true;

                    // Tạo màn mới
                    genBrickAndCapsule();
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

    // Áp dụng hiệu ứng khi paddle chạm capsule
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
        else if (type.equals("fastBall")) EffectManager.updateSpeed(ball, 1.5);
        else if (type.equals("slowBall")) EffectManager.updateSpeed(ball, 0.5);
        else if (type.equals("fireBall")) EffectManager.activateFireBall(ball);
        else if (type.equals("powerBall")) EffectManager.updatePower(ball, 3.0);
        else if (type.equals("expandPaddle")) EffectManager.changeWidth(paddle, 2.0);
        else if (type.equals("shrinkPaddle")) EffectManager.changeWidth(paddle, 0.5);
        else if (type.equals("explosion")) {
            showExplosion(capsule.getX(), capsule.getY());
            loseLife(); // Mất mạng khi dính bom
        }

        highestScore = Math.max(score, highestScore);
        if (capsule != null) {
            capsule.playSound();
        }
    }

    // Hiệu ứng nổ khi dính capsule "explosion"
    private void showExplosion(double x, double y) {
        Image explosionImage = new Image("file:resources/explosion.gif");
        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setFitWidth(100);
        explosionView.setFitHeight(100);
        explosionView.setX(x);
        explosionView.setY(y);
        root.getChildren().add(explosionView);

        // Xóa sau 2 giây
        PauseTransition removeDelay = new PauseTransition(Duration.seconds(2));
        removeDelay.setOnFinished(event -> root.getChildren().remove(explosionView));
        removeDelay.play();
    }

    // Đặt lại kích thước paddle về mặc định
    private void setPaddleDefault() {
        paddle.setWidth(widthP);
        paddle.setHeight(heightP);
    }

    // Đặt lại bóng về trạng thái ban đầu
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

    // Mất 1 mạng khi bóng rơi
    private void loseLife() {
        lives--;
        if (lives > 0) {
            // Xóa 1 trái tim
            if (!heartImages.isEmpty()) {
                ImageView lastHeart = heartImages.remove(heartImages.size() - 1);
                root.getChildren().remove(lastHeart);
            }
            // Reset vị trí bóng và paddle
            setPaddleDefault();
            setBallDefault();
            isAttached = true;
        } else {
            // Game over
            gameLoop.stop();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            saveHighestScore();
            Platform.runLater(() -> {
                primaryStage.close();
                EndMenu.show(score, highestScore); // Hiển thị màn kết thúc
            });
        }
    }

    // Lưu điểm cao nhất vào file
    private static void saveHighestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Path.highestScore))) {
            writer.write(String.valueOf(highestScore));
            System.out.println("Đã lưu highestScore: " + highestScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tạo và hiển thị cửa sổ game
    public static void createAndShowGame() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            MainGame game = new MainGame();
            game.start(stage);
        });
    }

    // Lấy điểm cao nhất
    public static int getBestScore() {
        return highestScore;
    }

    // Main: đọc điểm cao nhất từ file, hiển thị menu
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Path.highestScore))) {
            String line = reader.readLine();
            if (line != null) highestScore = Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        GameMenu.showMenu(); // Hiển thị menu chính
    }
}