// File: EndMenu.java
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.net.URL;
import java.io.File;

public class EndMenu {
    private static Stage stage; // Cửa sổ Game Over
    private static int score; // Điểm hiện tại
    private static int currentLevel; // Level hiện tại
    private static MediaPlayer mediaPlayer; // Phát nhạc kết thúc
    private static AudioClip mouseClickSound; // Âm thanh khi di chuột vào nút
    public static final int WIDTH_END = 1000;
    public static final int HEIGHT_END = 400;

    // Khởi tạo âm thanh click chuột (chỉ chạy 1 lần khi class được nạp)
    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    // Tải hình "Game Over" từ classpath hoặc file hệ thống
    private static Image loadGameOverImage() {
        return ScaleManager.loadImage(Path.gameOverImage.substring(1));
    }

    // Hiển thị màn hình Game Over
    public static void show(int score, int currentLevel) {
        EndMenu.score = score;
        EndMenu.currentLevel = currentLevel;
        // Đảm bảo chạy trên luồng JavaFX
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(score, currentLevel));
            return;
        }
        stage = new Stage();
        stage.setTitle("Game Over");
        stage.setResizable(false);
        // === LAYER 1: Hình nền (GIF) ===
        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH_END);
            bgView.setFitHeight(HEIGHT_END);
            bgView.setPreserveRatio(false); // Kéo giãn vừa khung
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;"); // Nền đen nếu không có ảnh
        }
        // === LAYER 2: Nội dung (trên nền) ===
        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");
        // Tiêu đề "Game Over"
        ImageView titleImageView = new ImageView(loadGameOverImage());
        if (titleImageView.getImage() != null) {
            titleImageView.setFitWidth(450);
            titleImageView.setPreserveRatio(true);
            titleImageView.setSmooth(true);
            titleImageView.setCache(true); // Tăng hiệu suất render
        }
        BorderPane.setAlignment(titleImageView, Pos.CENTER);
        contentPane.setTop(titleImageView);
        // Hiệu ứng nhấp nhô (phóng to/thu nhỏ liên tục)
        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(1.5), titleImageView);
        titleScale.setFromX(0.8);
        titleScale.setFromY(0.8);
        titleScale.setToX(1.01);
        titleScale.setToY(1.01);
        titleScale.setCycleCount(ScaleTransition.INDEFINITE);
        titleScale.setAutoReverse(true);
        titleScale.play();
        // === PHẦN GIỮA: ĐIỂM SỐ VÀ LEVEL ===
        VBox centerBox = new VBox(18);
        centerBox.setAlignment(Pos.CENTER);

        // HIỆU ỨNG GLOW + SHADOW
        String glowShadowStyle = "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
                "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        // Score Text
        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font("Arial", 28));
        scoreText.setFill(Color.rgb(206, 245, 129, 0.95));
        scoreText.setStyle(glowShadowStyle);

        // Current Level Text
        Text currentLevelText = new Text("Current Level: " + currentLevel);
        currentLevelText.setFont(Font.font("Arial", 28));
        currentLevelText.setFill(Color.rgb(206, 245, 129, 0.95));
        currentLevelText.setStyle(glowShadowStyle);

        centerBox.getChildren().addAll(scoreText, currentLevelText);
        contentPane.setCenter(centerBox);

        // === PHẦN DƯỚI: NÚT BẤM (SỬ DỤNG ImageButton GIỐNG CẢ HAI) ===
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: transparent;");

        // Tải ảnh nút xám và font
        Image greyBtn = loadImage("grey_button.png");
        Font btnFont = Font.font("Arial", 20);

        // Tạo các nút với ImageButton
        ImageButton playAgainBtn = new ImageButton(greyBtn, "Play Again", btnFont, mouseClickSound, 180);
        ImageButton exitBtn = new ImageButton(greyBtn, "Exit", btnFont, mouseClickSound, 180);

        // === SỰ KIỆN NÚT (BẮT ĐẦU TỪ LEVEL 1) ===
        playAgainBtn.setOnAction(() -> {
            stopMusicAndClose();
            MainGame.createAndShowGame(1); // Bắt đầu game mới từ level 1
        });

        exitBtn.setOnAction(() -> {
            stopMusicAndClose();
            Platform.exit();
            System.exit(0); // Thoát hoàn toàn
        });
        HBox buttonBox = new HBox(15, playAgainBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);
        contentPane.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));
        // === GỘP TẤT CẢ ===
        stackRoot.getChildren().add(contentPane);
        Scene scene = new Scene(stackRoot, WIDTH_END, HEIGHT_END);
        stage.setScene(scene);
        stage.setWidth(WIDTH_END);
        stage.setHeight(HEIGHT_END);
        // Phát nhạc kết thúc
        playBackgroundMusic();
        stage.show();
    }

    // Dừng nhạc và đóng stage
    private static void stopMusicAndClose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            VolumeManager.unregisterMediaPlayer(mediaPlayer);
        }
        if (stage != null) {
            stage.close();
        }
    }

    // Phát nhạc "The End" (chỉ 1 lần, lặp vô hạn)
    private static void playBackgroundMusic() {
        try {
            URL soundURL = EndMenu.class.getClassLoader().getResource(Path.theEndMusic.substring(1));
            Media media = soundURL != null ?
                    new Media(soundURL.toString()) :
                    new Media(Path.getFileURL(Path.theEndMusic));

            mediaPlayer = new MediaPlayer(media);
            VolumeManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp vô hạn
            mediaPlayer.setVolume(VolumeManager.getBackgroundVolume()); // Đặt volume
            mediaPlayer.play();

            System.out.println("Đang phát nhạc kết thúc (loop)");
        } catch (Exception e) {
            System.err.println("Không thể phát nhạc kết thúc: " + Path.theEndMusic);
            e.printStackTrace();
        }
    }

    // Tải hình nền động (end.gif) từ classpath hoặc file
    private static Image loadBackgroundImage() {
        return ScaleManager.loadAnimatedImage("end.gif");
    }

    // Tải ảnh từ resources (giống trong GameMenu)
    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }
}