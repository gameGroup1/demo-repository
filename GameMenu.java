// GameMenuFX.java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class GameMenu extends Application {
    private static Stage stage;
    private static MediaPlayer mediaPlayer;
    private static AudioClip mouseClickSound;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Arkanoid - Start Menu");
        stage.setResizable(false);

        StackPane root = new StackPane();
        Image bg = loadBackground();
        if (bg != null) {
            ImageView bgView = new ImageView(bg);
            bgView.setFitWidth(1100);
            bgView.setFitHeight(500);
            bgView.setPreserveRatio(false);
            root.getChildren().add(bgView);
        } else {
            root.setStyle("-fx-background-color: #1a1a1a;");
        }

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Title
        javafx.scene.text.Text title = new javafx.scene.text.Text("-ARKANOID-");
        title.setFont(Font.loadFont(getFontURL(), 48));
        title.setFill(Color.web("#9acd32"));
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 5, 0, 2, 2);");

        // Buttons
        Image greenBtn = loadImage("green_button.png");
        Font btnFont = Font.font("Arial", 32);

        ImageButton startBtn = new ImageButton(greenBtn, "Start", btnFont, mouseClickSound, 270);
        ImageButton bestBtn = new ImageButton(greenBtn, "Best Score", btnFont, mouseClickSound, 270);
        ImageButton exitBtn = new ImageButton(greenBtn, "Exit", btnFont, mouseClickSound, 270);

        startBtn.setOnAction(() -> {
            stopMusic();
            stage.close();
            MainGame.createAndShowGame();
        });

        bestBtn.setOnAction(() -> showBestScore());
        exitBtn.setOnAction(() -> {
            stopMusic();
            Platform.exit();
            System.exit(0);
        });

        // === THANH ÂM LƯỢNG ===
        // Background Volume
        Label bgLabel = new Label("Background Volume: " + (int)(SoundManager.getBackgroundVolume() * 100) + "%");
        bgLabel.setFont(Font.font("Arial", 16));
        bgLabel.setTextFill(Color.web("#9acd32"));

        Slider bgSlider = new Slider(0, 100, SoundManager.getBackgroundVolume() * 100);
        bgSlider.setPrefWidth(200);
        bgSlider.setMajorTickUnit(25);
        bgSlider.setShowTickMarks(true);
        bgSlider.setStyle("-fx-control-inner-background: #1a1a1a; -fx-accent: #9acd32;");
        bgSlider.valueProperty().addListener((obs, old, val) -> {
            double volume = val.doubleValue() / 100.0;
            SoundManager.setBackgroundVolume(volume);
            bgLabel.setText("Background Volume: " + val.intValue() + "%");
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume);
            }
        });

        // Effect Volume
        Label effectLabel = new Label("Effect Volume: " + (int)(SoundManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 16));
        effectLabel.setTextFill(Color.web("#9acd32"));

        Slider effectSlider = new Slider(0, 100, SoundManager.getEffectVolume() * 100);
        effectSlider.setPrefWidth(200);
        effectSlider.setMajorTickUnit(25);
        effectSlider.setShowTickMarks(true);
        effectSlider.setStyle("-fx-control-inner-background: #1a1a1a; -fx-accent: #abde46ff;");
        effectSlider.valueProperty().addListener((obs, old, val) -> {
            double volume = val.doubleValue() / 100.0;
            SoundManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + val.intValue() + "%");
        });

        VBox controls = new VBox(10, bgLabel, bgSlider, effectLabel, effectSlider);
        controls.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, startBtn, bestBtn, exitBtn, controls);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 1100, 500);
        stage.setScene(scene);
        playBackgroundMusic();
        stage.show();
    }

    private void showBestScore() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Best Score");
        alert.setHeaderText(null);
        alert.setContentText("Best Score: " + MainGame.getBestScore());
        alert.showAndWait();
    }

    private void playBackgroundMusic() {
        try {
            URL url = getClass().getClassLoader().getResource(Path.menuMusic.substring(1));
            Media media = url != null ? new Media(url.toString()) : new Media(Path.getFileURL(Path.menuMusic));
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(SoundManager.getBackgroundVolume());
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không phát được nhạc menu");
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            SoundManager.unregisterMediaPlayer(mediaPlayer);
        }
    }

    private Image loadBackground() {
        try {
            URL url = getClass().getClassLoader().getResource("background.gif");
            if (url != null) return new Image(url.toString(), true);
            return new Image("file:resources/background.gif", true);
        } catch (Exception e) {
            return null;
        }
    }

    private Image loadImage(String name) {
        try {
            URL url = getClass().getClassLoader().getResource(name);
            if (url != null) return new Image(url.toString());
            return new Image("file:resources/" + name);
        } catch (Exception e) {
            System.err.println("Không tải được " + name);
            return null;
        }
    }

    private String getFontURL() {
        try {
            URL url = getClass().getClassLoader().getResource("Monotype_corsiva.ttf");
            if (url != null) return url.toString();
            return "file:resources/Monotype_corsiva.ttf";
        } catch (Exception e) {
            return null;
        }
    }

    public static void showMenu() {
        new JFXPanel(); // Khởi động JavaFX Toolkit
        Platform.runLater(() -> {
            new GameMenu().start(new Stage());
        });
    }
}