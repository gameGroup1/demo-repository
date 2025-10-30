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
    private static Stage stage;           // Cửa sổ Game Over
    private static int score;             // Điểm hiện tại
    private static int bestScore;         // Điểm cao nhất
    private static MediaPlayer mediaPlayer; // Phát nhạc kết thúc
    private static AudioClip mouseClickSound; // Âm thanh khi di chuột vào nút

    // Khởi tạo âm thanh click chuột (chỉ chạy 1 lần khi class được nạp)
    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.MouseClick));
        SoundManager.registerAudioClip(mouseClickSound);
    }

    // Tải hình "Game Over" từ classpath hoặc file hệ thống
    private static Image loadGameOverImage() {
        Image gameOverImage = null;
        try {
            // 1. Thử tải từ classpath (trong JAR)
            URL imageURL = EndMenu.class.getClassLoader().getResource(Path.GameOverImage.substring(1));
            if (imageURL != null) {
                gameOverImage = new Image(imageURL.toString(), true);
                System.out.println("Đã tải game-over.png từ classpath");
                return gameOverImage;
            }

            // 2. Nếu không có → thử từ file hệ thống
            String fileURL = Path.getFileURL(Path.GameOverImage);
            System.out.println("Đang thử tải từ file: " + fileURL);
            gameOverImage = new Image(fileURL, true);

            // Bắt lỗi nếu ảnh không tải được
            gameOverImage.errorProperty().addListener((obs, old, error) -> {
                if (error) {
                    System.err.println("Lỗi: Không thể tải game-over.png từ hệ thống");
                }
            });

            System.out.println("Đã tải game-over.png từ file");
            return gameOverImage;

        } catch (Exception e) {
            System.err.println("Không thể tải game-over.png: " + Path.GameOverImage);
            e.printStackTrace();
        }
        return gameOverImage; // Trả về null nếu thất bại
    }

    // Hiển thị màn hình Game Over
    public static void show(int score, int bestScore) {
        EndMenu.score = score;
        EndMenu.bestScore = bestScore;

        // Đảm bảo chạy trên luồng JavaFX
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(score, bestScore));
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
            bgView.setFitWidth(768);
            bgView.setFitHeight(256);
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
        titleImageView.setFitWidth(450);
        titleImageView.setPreserveRatio(true);
        titleImageView.setSmooth(true);
        titleImageView.setCache(true); // Tăng hiệu suất render

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

        // === PHẦN GIỮA: ĐIỂM SỐ ===
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

        // === PHẦN DƯỚI: NÚT BẤM ===
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: transparent;");

        Button playAgainBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        // Style nút: trong suốt, chữ trắng
        String btnStyle = "-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; " +
                          "-fx-background-color: transparent; -fx-text-fill: white;";
        playAgainBtn.setStyle(btnStyle);
        exitBtn.setStyle(btnStyle);

        // === HIỆU ỨNG DI Nible: phóng to + âm thanh khi di chuột vào ===
        playAgainBtn.setOnMouseEntered(e -> {
            playAgainBtn.setScaleX(1.1);
            playAgainBtn.setScaleY(1.1);
            if (mouseClickSound != null) {
                mouseClickSound.play(SoundManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav chưa được tải");
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
            }
        });
        exitBtn.setOnMouseExited(e -> {
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        // Hiệu ứng khi nút được focus (dùng phím Tab)
        playAgainBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            playAgainBtn.setScaleX(newVal ? 1.1 : 1.0);
            playAgainBtn.setScaleY(newVal ? 1.1 : 1.0);
        });
        exitBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            exitBtn.setScaleX(newVal ? 1.1 : 1.0);
            exitBtn.setScaleY(newVal ? 1.1 : 1.0);
        });

        // === SỰ KIỆN NÚT ===
        playAgainBtn.setOnAction(e -> {
            stopMusicAndClose();
            startNewGame(); // Bắt đầu game mới
        });

        exitBtn.setOnAction(e -> {
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

        Scene scene = new Scene(stackRoot, 768, 246);
        stage.setScene(scene);

        // Phát nhạc kết thúc
        playBackgroundMusic();

        stage.show();
    }

    // Dừng nhạc và đóng stage
    private static void stopMusicAndClose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            SoundManager.unregisterMediaPlayer(mediaPlayer);
        }
        if (stage != null) {
            stage.close();
        }
    }

    // Phát nhạc "The End" (chỉ 1 lần)
    private static void playBackgroundMusic() {
        try {
            URL soundURL = EndMenu.class.getClassLoader().getResource(Path.theEndMusic.substring(1));
            Media media;
            if (soundURL != null) {
                media = new Media(soundURL.toString());
                System.out.println("Phát TheEnd.wav từ classpath");
            } else {
                media = new Media(Path.getFileURL(Path.theEndMusic));
                System.out.println("Phát TheEnd.wav từ: " + Path.theEndMusic);
            }
            mediaPlayer = new MediaPlayer(media);
            SoundManager.registerMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(1); // Chỉ phát 1 lần
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Không tìm thấy TheEnd.wav: " + Path.theEndMusic);
            e.printStackTrace();
        }
    }

    // Bắt đầu game mới
    private static void startNewGame() {
        MainGame.createAndShowGame();
    }

    // Tải hình nền động (end.gif) từ classpath hoặc file
    private static Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            URL imageURL = EndMenu.class.getClassLoader().getResource("end.gif");
            if (imageURL != null) {
                backgroundImage = new Image(imageURL.toString(), true);
                System.out.println("Đã tải end.gif từ classpath");
                return backgroundImage;
            }

            String[] paths = { "resources/end.gif", "./resources/end.gif", "../resources/end.gif" };
            for (String path : paths) {
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    backgroundImage = new Image(file.toURI().toString(), true);
                    System.out.println("Đã tải end.gif từ: " + path);
                    return backgroundImage;
                }
            }
            System.err.println("Không tìm thấy end.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }
}