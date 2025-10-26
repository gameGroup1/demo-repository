import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class EndMenu {
    private static Stage stage;
    private static int score;
    private static int bestScore;
    private static MediaPlayer mediaPlayer;
    private static AudioClip mouseClickSound;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.MouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }

    private static Image loadGameOverImage() {
   	Image gameOverImage = null;
    try {
        // 1. Thử load từ classpath (resources)
        URL imageURL = EndMenu.class.getClassLoader().getResource(Path.GameOverImage.substring(1)); // bỏ dấu /
        if (imageURL != null) {
            gameOverImage = new Image(imageURL.toString(), true);
            System.out.println("Loaded game-over.png from classpath: " + Path.GameOverImage);
            return gameOverImage;
        }

        // 2. Nếu không có trong classpath → dùng file hệ thống
        String fileURL = Path.getFileURL(Path.GameOverImage);
        System.out.println("Trying file path: " + fileURL);
        gameOverImage = new Image(fileURL, true);

        // Kiểm tra ảnh có load thành công không
        gameOverImage.errorProperty().addListener((obs, old, error) -> {
            if (error) {
                System.err.println("Failed to load game-over.png from file system");
            }
        });

        System.out.println("Loaded game-over.png from file: " + Path.GameOverImage);
        return gameOverImage;

    } catch (Exception e) {
        System.err.println("Cannot load game-over.png from: " + Path.GameOverImage);
        e.printStackTrace();
    }
    return gameOverImage;
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

        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView imageView = new ImageView(backgroundImage);
            imageView.setFitWidth(768);
            imageView.setFitHeight(256);
            imageView.setPreserveRatio(false);
            stackRoot.getChildren().add(imageView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;");
        }

        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");

        ImageView titleImageView = new ImageView(loadGameOverImage());
        titleImageView.setFitWidth(450);           // Tùy chỉnh kích thước
        titleImageView.setPreserveRatio(true);
        titleImageView.setSmooth(true);
        titleImageView.setCache(true);             // Tăng hiệu suất

        BorderPane.setAlignment(titleImageView, Pos.CENTER);
        contentPane.setTop(titleImageView);

        // === HIỆU ỨNG PHÓNG TO/THU NHỎ ===
        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(1.5), titleImageView);
        titleScale.setFromX(0.8);
        titleScale.setFromY(0.8);
        titleScale.setToX(1.01);
        titleScale.setToY(1.01);
        titleScale.setCycleCount(ScaleTransition.INDEFINITE);
        titleScale.setAutoReverse(true);
        titleScale.play();

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font("Arial", 24));
        scoreLabel.setTextFill(Color.WHITE);

        Label bestScoreLabel = new Label("Best Score: " + bestScore);
        bestScoreLabel.setFont(Font.font("Arial", 24));
        bestScoreLabel.setTextFill(Color.WHITE);

        centerBox.getChildren().addAll(scoreLabel, bestScoreLabel);
        contentPane.setCenter(centerBox);

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-border-color:transparent; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: transparent;");

        Button playAgainBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        playAgainBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: transparent; -fx-text-fill: white;");
        exitBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: transparent; -fx-text-fill: white;");

        playAgainBtn.setOnMouseEntered(e -> {
            playAgainBtn.setScaleX(1.1);
            playAgainBtn.setScaleY(1.1);
            if (mouseClickSound != null) {
                mouseClickSound.play(SoundManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav not loaded.");
            }
        });
        playAgainBtn.setOnMouseExited(e -> {
            playAgainBtn.setScaleX(1.0);
            playAgainBtn.setScaleY(1.0);
        });

        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setScaleX(1.1);
            exitBtn.setScaleY(1.1);
            if (mouseClickSound != null) {
                mouseClickSound.play(SoundManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav not loaded.");
            }
        });
        exitBtn.setOnMouseExited(e -> {
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        playAgainBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                playAgainBtn.setScaleX(1.1);
                playAgainBtn.setScaleY(1.1);
            } else {
                playAgainBtn.setScaleX(1.0);
                playAgainBtn.setScaleY(1.0);
            }
        });

        exitBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                exitBtn.setScaleX(1.1);
                exitBtn.setScaleY(1.1);
            } else {
                exitBtn.setScaleX(1.0);
                exitBtn.setScaleY(1.0);
            }
        });

        playAgainBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            stage.close();
            startNewGame();
        });

        exitBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                SoundManager.unregisterMediaPlayer(mediaPlayer);
            }
            stage.close();
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playAgainBtn, exitBtn);

        bottomBox.getChildren().add(buttonBox);
        contentPane.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        stackRoot.getChildren().add(contentPane);

        Scene scene = new Scene(stackRoot, 768, 246);
        stage.setScene(scene);

        // Play background music
        playBackgroundMusic();

        stage.show();
    }

    private static void playBackgroundMusic() {
        try {
            URL soundURL = EndMenu.class.getClassLoader().getResource(Path.theEndMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("✓ Playing TheEnd.wav from classpath");
            } else {
                media = new Media(Path.getFileURL(Path.theEndMusic));
                System.out.println("✓ Playing TheEnd.wav from: " + Path.theEndMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(1);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("✗ Cannot find TheEnd.wav at " + Path.theEndMusic);
            e.printStackTrace();
        }
    }

    private static void startNewGame() {
        MainGame.createAndShowGame();
    }

    private static Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            URL imageURL = EndMenu.class.getClassLoader().getResource("end.gif");
            if (imageURL != null) {
                backgroundImage = new Image(imageURL.toString(), true);
                System.out.println("✓ Loaded from classpath");
                return backgroundImage;
            }
            String[] paths = {
                    "resources/end.gif",
                    "./resources/end.gif",
                    "../resources/end.gif"
            };
            for (String path : paths) {
                java.io.File file = new java.io.File(path);
                System.out.println("Trying: " + file.getAbsolutePath());
                if (file.exists()) {
                    backgroundImage = new Image(file.toURI().toString(), true);
                    System.out.println("✓ Loaded from: " + path);
                    return backgroundImage;
                }
            }
            System.err.println("✗ Cannot find end.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }
}