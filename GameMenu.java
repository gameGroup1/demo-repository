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
        VolumeManager.registerAudioClip(mouseClickSound);
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
        ImageButton settingBtn = new ImageButton(greenBtn, "Settings", btnFont, mouseClickSound, 270);
        ImageButton exitBtn = new ImageButton(greenBtn, "Exit", btnFont, mouseClickSound, 270);

        startBtn.setOnAction(() -> {
            stopMusic();
            stage.close();
            MainGame.createAndShowGame();
        });

        bestBtn.setOnAction(() -> showBestScore());
        settingBtn.setOnAction(() -> showSettings());
        exitBtn.setOnAction(() -> {
            stopMusic();
            Platform.exit();
            System.exit(0);
        });

        content.getChildren().addAll(title, startBtn, bestBtn, settingBtn, exitBtn);
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

    private void showSettings() {
        Stage settingsStage = new Stage();
        settingsStage.initOwner(stage);
        settingsStage.setTitle("Settings");
        settingsStage.setResizable(false);

        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(30));
        settingsBox.setStyle("-fx-background-color: #003200;");

        bestBtn.setOnAction(() -> showBestScore());
        exitBtn.setOnAction(() -> {
            stopMusic();
            Platform.exit();
            System.exit(0);
        });

        // === THANH ÂM LƯỢNG ===
        // Background Volume
        Label bgLabel = new Label("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        bgLabel.setFont(Font.font("Arial", 16));
        bgLabel.setTextFill(Color.WHITE);

        Slider bgSlider = new Slider(0, 100, VolumeManager.getBackgroundVolume() * 100);
        bgSlider.setPrefWidth(300);
        bgSlider.setMajorTickUnit(25);
        bgSlider.setShowTickMarks(true);
        bgSlider.setStyle("-fx-control-inner-background: #1a1a1a; -fx-accent: #9acd32;");
        bgSlider.valueProperty().addListener((obs, old, val) -> {
            double volume = val.doubleValue() / 100.0;
            VolumeManager.setBackgroundVolume(volume);
            bgLabel.setText("Background Volume: " + val.intValue() + "%");
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume);
            }
        });

        // Effect Volume
        Label effectLabel = new Label("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 16));
        effectLabel.setTextFill(Color.WHITE);

        Slider effectSlider = new Slider(0, 100, VolumeManager.getEffectVolume() * 100);
        effectSlider.setPrefWidth(300);
        effectSlider.setMajorTickUnit(25);
        effectSlider.setShowTickMarks(true);
        effectSlider.setStyle("-fx-control-inner-background: #1a1a1a; -fx-accent: #9acd32;");
        effectSlider.valueProperty().addListener((obs, old, val) -> {
            double volume = val.doubleValue() / 100.0;
            VolumeManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + val.intValue() + "%");
        });

        // Close Button
        javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("Close");
        closeBtn.setFont(Font.font("Arial", 18));
        closeBtn.setStyle("-fx-background-color: #9acd32; -fx-text-fill: black; -fx-padding: 10 30;");
        closeBtn.setOnAction(e -> settingsStage.close());

        VBox bgVolumeBox = new VBox(10, bgLabel, bgSlider);
        bgVolumeBox.setAlignment(Pos.CENTER);
        
        VBox effectVolumeBox = new VBox(10, effectLabel, effectSlider);
        effectVolumeBox.setAlignment(Pos.CENTER);

        settingsBox.getChildren().addAll(bgVolumeBox, effectVolumeBox, closeBtn);

        Scene settingsScene = new Scene(settingsBox, 400, 300);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    private void playBackgroundMusic() {
        try {
            URL url = getClass().getClassLoader().getResource(Path.menuMusic.substring(1));
            Media media = url != null ? new Media(url.toString()) : new Media(Path.getFileURL(Path.menuMusic));
            mediaPlayer = new MediaPlayer(media);
            VolumeManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(VolumeManager.getBackgroundVolume());
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không phát được nhạc menu");
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            VolumeManager.unregisterMediaPlayer(mediaPlayer);
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
