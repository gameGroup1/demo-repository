import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.io.File;

public class Pause {

    private static AudioClip mouseClickSound;
    public static final int WIDTH_PAUSE = 400;
    public static final int HEIGHT_PAUSE = 300;

    // Khởi tạo âm thanh click chuột
    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    // Tải ảnh từ resources sử dụng ScaleManager
    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }

    // Tải hình nền GIF cho pause menu
    private static Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            // 1. Thử tải từ classpath (trong JAR)
            URL imageURL = Pause.class.getClassLoader().getResource("PauseMenu.gif");
            if (imageURL != null) {
                backgroundImage = new Image(imageURL.toString(), true);
                System.out.println("Đã tải PauseMenu.gif từ classpath");
                return backgroundImage;
            }
            // 2. Nếu không có → thử từ file hệ thống
            String[] paths = { "resources/PauseMenu.gif", "./resources/PauseMenu.gif", "../resources/PauseMenu.gif" };
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    backgroundImage = new Image(file.toURI().toString(), true);
                    System.out.println("Đã tải PauseMenu.gif từ: " + path);
                    return backgroundImage;
                }
            }
            System.err.println("Không tìm thấy PauseMenu.gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
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

        // === LAYER 1: Hình nền (GIF) ===
        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH_PAUSE);
            bgView.setFitHeight(HEIGHT_PAUSE);
            bgView.setPreserveRatio(false); // Kéo giãn vừa khung
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;"); // Nền đen nếu không có ảnh
        }

        // === LAYER 2: Nội dung (trên nền) ===
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent; -fx-padding: 30;");
        contentBox.setPrefSize(WIDTH_PAUSE, HEIGHT_PAUSE);

        // === TIÊU ĐỀ ===
        Label titleLabel = new Label("PAUSED");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.CYAN);
        VBox.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // === PHẦN GIỮA: SLIDER ÂM THANH ===
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(350);
        centerBox.setStyle("-fx-background-color: rgba(59, 59, 59, 0.8); -fx-background-radius: 10; -fx-padding: 15;");

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
        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: rgba(59, 59, 59, 0.8); -fx-background-radius: 10;");
        bottomBox.setPrefWidth(WIDTH_PAUSE);

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
        HBox buttonBox = new HBox(20, continueBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);

        // === THÊM TẤT CẢ VÀO CONTENT BOX ===
        contentBox.getChildren().addAll(titleLabel, centerBox, bottomBox);

        // === GỘP TẤT CẢ VÀO STACK ROOT ===
        stackRoot.getChildren().add(contentBox);

        // === TẠO SCENE ===
        Scene scene = new Scene(stackRoot, WIDTH_PAUSE, HEIGHT_PAUSE);
        pauseStage.setScene(scene);
        pauseStage.showAndWait();
    }
}