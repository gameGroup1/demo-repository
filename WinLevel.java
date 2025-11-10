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

public class WinLevel {

    private static AudioClip mouseClickSound;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

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
            URL imageURL = Pause.class.getClassLoader().getResource("WinLevel.gif");
            if (imageURL != null) {
                backgroundImage = new Image(imageURL.toString(), true);
                System.out.println("Đã tải WinLevel.gif từ classpath");
                return backgroundImage;
            }
            // 2. Nếu không có → thử từ file hệ thống
            String[] paths = { "ImageMenu/resources/WinLevel.gif", "./resources/WinLevel.gif", "../resources/WinLevel.gif" };
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    backgroundImage = new Image(file.toURI().toString(), true);
                    System.out.println("Đã tải WinLevel.gif từ: " + path);
                    return backgroundImage;
                }
            }
            System.err.println("Không tìm thấy WinLevel.gif");
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

        Stage winStage = new Stage();
        winStage.setTitle("Pause");
        winStage.initModality(Modality.APPLICATION_MODAL);
        winStage.initOwner(parentStage);
        winStage.setResizable(false);

        // === LAYER 1: Hình nền (GIF) ===
        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH);
            bgView.setFitHeight(HEIGHT);
            bgView.setPreserveRatio(false);
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;");
        }

        // === LAYER 2: Nội dung (trên nền) ===
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent; -fx-padding: 30;");
        contentBox.setPrefSize(WIDTH, HEIGHT);

        // === TẢI ẢNH NÚT ===
        Image greyBtnImage = loadImage("grey_button.png");
        if (greyBtnImage == null) {
            System.err.println("Lỗi: Không tải được grey_button.png");
            greyBtnImage = new Image("file:ImageGame/resources/grey_button.png");
        }

        Image greenBtnImage = loadImage("green_button.png");
        if (greenBtnImage == null) {
            System.err.println("Lỗi: Không tải được green_button.png");
            greenBtnImage = new Image("file:ImageGame/resources/green_button.png");
        }

        Font btnFont = Font.font("Arial", 20);

        // === TIÊU ĐỀ: PAUSED (dùng green_button.png) ===
        ImageButton titleButton = new ImageButton(greenBtnImage, "WIN LEVEL", Font.font("Arial", 43), null, 400);
        titleButton.setMouseTransparent(true);
        titleButton.setOnAction(() -> {});
        titleButton.getChildren().stream()
                .filter(node -> node instanceof javafx.scene.text.Text)
                .map(node -> (javafx.scene.text.Text) node)
                .forEach(text -> text.setFill(Color.rgb(206, 245, 129, 0.8)));
        VBox.setMargin(titleButton, new Insets(0, 0, 30, 0));

        // === PHẦN GIỮA: SLIDER ÂM THANH ===
        VBox centerBox = new VBox(18);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(400);
        centerBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-background-radius: 15; -fx-padding: 20;");

        // Nhãn Background Volume (dùng green_button)
        ImageButton backgroundLabelBtn = new ImageButton(greenBtnImage,
                "Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%",
                Font.font("Arial", 16), null, 340);
        backgroundLabelBtn.setMouseTransparent(true);
        backgroundLabelBtn.setOnAction(() -> {});
        backgroundLabelBtn.getChildren().stream()
                .filter(node -> node instanceof javafx.scene.text.Text)
                .map(node -> (javafx.scene.text.Text) node)
                .forEach(text -> text.setFill(Color.rgb(206, 245, 129, 0.8)));
        // === NÚT BẤM ===
        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15;");

        // Tạo nút Continue & Exit (dùng grey_button)
        ImageButton continueBtn = new ImageButton(greyBtnImage, "Next Level", btnFont, mouseClickSound, 170);
        ImageButton exitBtn = new ImageButton(greyBtnImage, "Exit", btnFont, mouseClickSound, 170);

        // Hành động nút Continue
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

            winStage.close();
        });

        // Hành động nút Exit
        exitBtn.setOnAction(() -> {
            MainGame.cleanup();
            winStage.close();
            if (parentStage != null) {
                parentStage.close();
            }
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(25, continueBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);

        // === GỘP TẤT CẢ ===
        contentBox.getChildren().addAll(titleButton, centerBox, bottomBox);
        stackRoot.getChildren().add(contentBox);

        // === TẠO SCENE ===
        Scene scene = new Scene(stackRoot, WIDTH, HEIGHT);
        scene.getStylesheets().add(Pause.class.getResource("/styles.css") != null ?
                Pause.class.getResource("/styles.css").toExternalForm() : "");
        winStage.setScene(scene);
        winStage.showAndWait();
    }
}