import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
    private static Stage stage;
    private static int score;
    private static int bestScore;
    private static MediaPlayer mediaPlayer;
    private static AudioClip mouseClickSound;
    public static final int WIDTH_END = 1000;
    public static final int HEIGHT_END = 400;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    private static Image loadGameOverImage() {
        return ScaleManager.loadImage(Path.gameOverImage.substring(1));
    }

    public static void show(int score, int bestScore) {
        EndMenu.score = score;
        EndMenu.bestScore = bestScore;
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(score, bestScore));
            return;
        }

        stage = new Stage();
        stage.setTitle("Game Over");
        stage.setResizable(false);

        // === LAYER 1: HÌNH NỀN GIF ===
        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH_END);
            bgView.setFitHeight(HEIGHT_END);
            bgView.setPreserveRatio(false);
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;");
        }

        // === LAYER 2: NỘI DUNG ===
        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");

        // === TIÊU ĐỀ "Game Over" ===
        ImageView titleImageView = new ImageView(loadGameOverImage());
        if (titleImageView.getImage() != null) {
            titleImageView.setFitWidth(450);
            titleImageView.setPreserveRatio(true);
            titleImageView.setSmooth(true);
            titleImageView.setCache(true);
        }
        BorderPane.setAlignment(titleImageView, Pos.CENTER);
        contentPane.setTop(titleImageView);

        // Animation nhấp nhô
        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(1.5), titleImageView);
        titleScale.setFromX(0.8);
        titleScale.setFromY(0.8);
        titleScale.setToX(1.01);
        titleScale.setToY(1.01);
        titleScale.setCycleCount(ScaleTransition.INDEFINITE);
        titleScale.setAutoReverse(true);
        titleScale.play();

        // === PHẦN GIỮA: SCORE TEXT (GIỐNG HỆT PAUSE) ===
        VBox centerBox = new VBox(18);
        centerBox.setAlignment(Pos.CENTER);

        // HIỆU ỨNG GLOW + SHADOW (COPY 100% TỪ PAUSE)
        String glowShadowStyle = "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
            "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        // Score Text
        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font("Arial", 28));
        scoreText.setFill(Color.rgb(206, 245, 129, 0.95));
        scoreText.setStyle(glowShadowStyle);

        // Best Score Text
        Text bestScoreText = new Text("Best Score: " + bestScore);
        bestScoreText.setFont(Font.font("Arial", 28));
        bestScoreText.setFill(Color.rgb(206, 245, 129, 0.95)); // ← GIỐNG PAUSE, KHÔNG DÙNG XANH
        bestScoreText.setStyle(glowShadowStyle);

        centerBox.getChildren().addAll(scoreText, bestScoreText);
        contentPane.setCenter(centerBox);

        // === PHẦN DƯỚI: NÚT ImageButton ===
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: transparent;");

        Image greyBtn = loadImage("grey_button.png");
        Font btnFont = Font.font("Arial", 20);

        ImageButton playAgainBtn = new ImageButton(greyBtn, "Play Again", btnFont, mouseClickSound, 180);
        ImageButton exitBtn = new ImageButton(greyBtn, "Exit", btnFont, mouseClickSound, 180);

        playAgainBtn.setOnAction(() -> {
            stopMusicAndClose();
            startNewGame();
        });

        exitBtn.setOnAction(() -> {
            stopMusicAndClose();
            Platform.exit();
            System.exit(0);
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

        playBackgroundMusic();
        stage.show();
    }

    private static void stopMusicAndClose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            VolumeManager.unregisterMediaPlayer(mediaPlayer);
        }
        if (stage != null) {
            stage.close();
        }
    }

    private static void playBackgroundMusic() {
        try {
            URL soundURL = EndMenu.class.getClassLoader().getResource(Path.theEndMusic.substring(1));
            Media media = soundURL != null ? 
                new Media(soundURL.toString()) : 
                new Media(Path.getFileURL(Path.theEndMusic));
            
            mediaPlayer = new MediaPlayer(media);
            VolumeManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(VolumeManager.getBackgroundVolume());
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không thể phát nhạc kết thúc: " + Path.theEndMusic);
            e.printStackTrace();
        }
    }

    private static void startNewGame() {
        MainGame.createAndShowGame();
    }

    private static Image loadBackgroundImage() {
        return ScaleManager.loadAnimatedImage("end.gif");
    }

    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }
}