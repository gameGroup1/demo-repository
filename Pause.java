import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;

import java.net.URL;

public class Pause {

    private static AudioClip mouseClickSound;

    // Khởi tạo âm thanh click chuột
    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    // Tải ảnh từ resources (giống trong GameMenu)
    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }

    public static void show(Stage parentStage, AnimationTimer gameLoop) {
        // Đảm bảo chạy trên luồng JavaFX
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
        pauseStage.initModality(Modality.APPLICATION_MODAL);
        pauseStage.initOwner(parentStage);
        pauseStage.setResizable(false);

        // === LAYOUT CHÍNH - Sử dụng VBox thay vì BorderPane ===
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 30;");
        root.setPrefSize(400, 300); // Tăng kích thước để đủ chỗ

        // === TIÊU ĐỀ ===
        Label titleLabel = new Label("PAUSED");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.CYAN);
        VBox.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // === PHẦN GIỮA: SLIDER ÂM THANH ===
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(350);

        Label backgroundLabel = new Label("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        backgroundLabel.setFont(Font.font("Arial", 16));
        backgroundLabel.setTextFill(Color.WHITE);

        Slider backgroundSlider = new Slider(0, 100, VolumeManager.getBackgroundVolume() * 100);
        backgroundSlider.setPrefWidth(300);
        backgroundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setBackgroundVolume(volume);
            backgroundLabel.setText("Background Volume: " + newVal.intValue() + "%");
        });

        Label effectLabel = new Label("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 16));
        effectLabel.setTextFill(Color.WHITE);

        Slider effectSlider = new Slider(0, 100, VolumeManager.getEffectVolume() * 100);
        effectSlider.setPrefWidth(300);
        effectSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + newVal.intValue() + "%");
        });

        centerBox.getChildren().addAll(backgroundLabel, backgroundSlider, effectLabel, effectSlider);

        // === PHẦN DƯỚI: NÚT BẤM ===
        // Thay vì dùng padding trong style, hãy dùng margin
        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 20 30; -fx-background-color: #3b3b3b; -fx-background-radius: 10;");
        bottomBox.setPrefWidth(400);

    

        // Tải ảnh nút
        Image greyBtnImage = loadImage("grey_button.png");
        if (greyBtnImage == null) {
            System.err.println("Lỗi: Không tải được grey_button.png");
            // Tạo ảnh placeholder để không bị lỗi
            greyBtnImage = new Image("file:resources/grey_button.png");
        }

        Font btnFont = Font.font("Arial", 20);

        // Tạo nút ImageButton
        ImageButton continueBtn = new ImageButton(greyBtnImage, "Continue", btnFont, mouseClickSound, 170);
        ImageButton exitBtn = new ImageButton(greyBtnImage, "Exit", btnFont, mouseClickSound, 170);

        // === HÀNH ĐỘNG NÚT CONTINUE ===
        continueBtn.setOnAction(() -> {
            if (gameLoop != null) {
                gameLoop.start();
            }

            Platform.runLater(() -> {
                MainGame.isPaused = false;
                parentStage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

                Paddle paddle = MainGame.staticPaddle;
                if (paddle != null) {
                    double paddleCenterX = paddle.getX();
                    double screenX = parentStage.getX() + paddleCenterX + 8;
                    double screenY = parentStage.getY() + paddle.getY() + 50;

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

        // === HÀNH ĐỘNG NÚT EXIT ===
        exitBtn.setOnAction(() -> {
            MainGame.cleanup();
            pauseStage.close();
            if (parentStage != null) {
                parentStage.close();
            }
            Platform.exit();
            System.exit(0);
        });
        // === GỘP NÚT VÀO HBOX ===
        HBox buttonBox = new HBox(0, continueBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);
        // === THÊM TẤT CẢ VÀO ROOT ===
        root.getChildren().addAll(titleLabel, centerBox, bottomBox);
        // === TẠO SCENE ===
        Scene scene = new Scene(root, 400, 300); // Tăng kích thước scene
        pauseStage.setScene(scene);
        pauseStage.showAndWait();
    }
}