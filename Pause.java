import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;

public class Pause {
    private static final double PAUSE_WIDTH = 300;
    private static final double PAUSE_HEIGHT = 350;
    private static AudioClip mouseClickSound;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    public static void show(Stage parentStage, AnimationTimer gameLoop) {
        // Kiểm tra xem JavaFX đã được khởi tạo chưa
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(parentStage, gameLoop));
            return;
        }

        // Tạm dừng game loop
        if (gameLoop != null) {
            gameLoop.stop();
        }

        Stage pauseStage = new Stage();
        pauseStage.setTitle("Pause");
        pauseStage.initModality(Modality.APPLICATION_MODAL); // Làm modal để block input cho parent
        pauseStage.initOwner(parentStage);
        pauseStage.setResizable(false);

        // Tạo layout chính
        BorderPane root = new BorderPane();
        // Không đăng ký root với ScaleManager để background giữ nguyên và window fixed size
        // ScaleManager.registerScalableNode(root);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20;");

        // Tiêu đề Pause
        Label titleLabel = new Label("PAUSED");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.CYAN);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // Phần giữa - Điều chỉnh âm thanh
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);

        // Background Volume
        Label backgroundLabel = new Label("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        backgroundLabel.setFont(Font.font("Arial", 16));
        backgroundLabel.setTextFill(Color.WHITE);
        Slider backgroundSlider = new Slider(0, 100, (int)(VolumeManager.getBackgroundVolume() * 100));
        backgroundSlider.setPrefWidth(200);
        backgroundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setBackgroundVolume(volume);
            backgroundLabel.setText("Background Volume: " + newVal.intValue() + "%");
        });

        // Effect Volume
        Label effectLabel = new Label("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 16));
        effectLabel.setTextFill(Color.WHITE);
        Slider effectSlider = new Slider(0, 100, (int)(VolumeManager.getEffectVolume() * 100));
        effectSlider.setPrefWidth(200);
        effectSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + newVal.intValue() + "%");
        });

        centerBox.getChildren().addAll(
                backgroundLabel, backgroundSlider,
                effectLabel, effectSlider
        );
        root.setCenter(centerBox);

        // Phần dưới - Các nút
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: #3b3b3b;");

        Button continueBtn = new Button("Continue");
        Button exitBtn = new Button("Exit");
        continueBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        exitBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #f44336; -fx-text-fill: white;");

        // Hiệu ứng scale và âm thanh khi hover cho continueBtn
        continueBtn.setOnMouseEntered(e -> {
            continueBtn.setScaleX(1.1);
            continueBtn.setScaleY(1.1);
            if (mouseClickSound != null) {
                mouseClickSound.play(VolumeManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav not loaded.");
            }
        });
        continueBtn.setOnMouseExited(e -> {
            continueBtn.setScaleX(1.0);
            continueBtn.setScaleY(1.0);
        });

        // Hiệu ứng scale và âm thanh khi hover cho exitBtn
        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setScaleX(1.1);
            exitBtn.setScaleY(1.1);
            if (mouseClickSound != null) {
                mouseClickSound.play(VolumeManager.getEffectVolume());
            } else {
                System.err.println("Mouse_Click.wav not loaded.");
            }
        });
        exitBtn.setOnMouseExited(e -> {
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        // Hiệu ứng scale khi focus (bàn phím) cho continueBtn
        continueBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                continueBtn.setScaleX(1.1);
                continueBtn.setScaleY(1.1);
            } else {
                continueBtn.setScaleX(1.0);
                continueBtn.setScaleY(1.0);
            }
        });

        // Hiệu ứng scale khi focus (bàn phím) cho exitBtn
        exitBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                exitBtn.setScaleX(1.1);
                exitBtn.setScaleY(1.1);
            } else {
                exitBtn.setScaleX(1.0);
                exitBtn.setScaleY(1.0);
            }
        });

        // Action cho Continue: Tiếp tục game
        continueBtn.setOnAction(e -> {
            if (gameLoop != null) {
                gameLoop.start();
            }
            Platform.runLater(() -> {
                // BẬT LẠI INPUT
                MainGame.isPaused = false;
                // HIỆN LẠI CON TRỚ
                parentStage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
                // ĐẶT CHUỘT VỀ GIỮA PADDLE
                Paddle paddle = MainGame.staticPaddle;
                if (paddle != null) {
                    double paddleCenterX = paddle.getX();// + paddle.getWidth() / 2;
                    double screenX = parentStage.getX() + paddleCenterX * ScaleManager.getScale() + 8;
                    double screenY = parentStage.getY() + paddle.getY() * ScaleManager.getScale() + 50; // Trên paddle, adjust với scale
                    try {
                        java.awt.Robot robot = new java.awt.Robot();
                        robot.mouseMove((int) screenX, (int) screenY);
                    } catch (Exception ex) {
                        System.out.println("Không thể di chuyển chuột tự động: " + ex.getMessage());
                    }
                }
            });
            pauseStage.close();
        });

        // Action cho Exit: Dừng toàn bộ chương trình
        exitBtn.setOnAction(e -> {
            System.out.println("Exit button pressed - Starting full shutdown...");
            // Dừng game loop nếu chưa dừng
            if (gameLoop != null) {
                gameLoop.stop();
                System.out.println("GameLoop stopped.");
            }
            // Gọi cleanup trực tiếp từ MainGame để dừng media và lưu điểm
            MainGame.cleanup();
            System.out.println("MainGame cleanup called.");
            // Đóng pause stage và parent stage
            pauseStage.close();
            if (parentStage != null) {
                parentStage.close();
                System.out.println("Stages closed.");
            }
            // Dừng tất cả âm thanh (đã gọi trong cleanup, nhưng đảm bảo)
            VolumeManager.stopAllSounds();
            System.out.println("All sounds stopped.");
            // Dừng JavaFX application thread
            Platform.exit();
            System.out.println("Platform exited.");
            // Buộc dừng toàn bộ JVM (dừng tất cả luồng)
            System.exit(0);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(continueBtn, exitBtn);
        bottomBox.getChildren().add(buttonBox);
        root.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(root, PAUSE_WIDTH, PAUSE_HEIGHT);
        pauseStage.setScene(scene);

        // Fixed size, không scale để background giữ nguyên
        pauseStage.setWidth(PAUSE_WIDTH);
        pauseStage.setHeight(PAUSE_HEIGHT);
        pauseStage.showAndWait(); // Chờ đến khi close mới tiếp tục
    }
}