// GameMenu.java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

        ImageButton newGameBtn = new ImageButton(greenBtn, "New Game", btnFont, mouseClickSound, 270);
        ImageButton continueBtn = new ImageButton(greenBtn, "Continue", btnFont, mouseClickSound, 270);
        ImageButton bestBtn = new ImageButton(greenBtn, "Best Level", btnFont, mouseClickSound, 270);
        ImageButton settingBtn = new ImageButton(greenBtn, "Settings", btnFont, mouseClickSound, 270);
        ImageButton exitBtn = new ImageButton(greenBtn, "Exit", btnFont, mouseClickSound, 270);

        newGameBtn.setOnAction(() -> {
            stopMusic();
            stage.close();
            MainGame.createAndShowGame(1);
        });

        continueBtn.setOnAction(() -> {
            int last = MainGame.getLastLevel();
            if (last > 5) { // Giả sử max level là 5
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("You are done, please click New Game");
                alert.showAndWait();
            } else {
                stopMusic();
                stage.close();
                MainGame.createAndShowGame(last);
            }
        });

        bestBtn.setOnAction(() -> BestLevelMenu.show());
        settingBtn.setOnAction(() -> showSettings());
        exitBtn.setOnAction(() -> {
            stopMusic();
            Platform.exit();
            System.exit(0);
        });

        content.getChildren().addAll(title, newGameBtn, continueBtn, bestBtn, settingBtn, exitBtn);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 1100, 500);
        stage.setScene(scene);
        playBackgroundMusic();
        stage.show();
    }

    private void showSettings() {
        SettingMenu.show(stage);
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
        return ScaleManager.loadAnimatedImage("background.gif");
    }

    private Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }

    private String getFontURL() {
        try {
            URL url = getClass().getClassLoader().getResource("Monotype_corsiva.ttf");
            if (url != null) return url.toString();
            return "file:ImageGame/resources/Monotype_corsiva.ttf";
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