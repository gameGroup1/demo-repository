package code_for_menu;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_object.*;
import code_for_update.*;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.io.File;

public class WinAllLevels {

    private static AudioClip mouseClickSound;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }

    private static Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            URL imageURL = Pause.class.getClassLoader().getResource("PauseMenu.gif");
            if (imageURL != null) {
                backgroundImage = new Image(imageURL.toString(), true);
                return backgroundImage;
            }
            String[] paths = { "resources/PauseMenu.gif", "./resources/PauseMenu.gif", "../resources/PauseMenu.gif" };
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    backgroundImage = new Image(file.toURI().toString(), true);
                    return backgroundImage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backgroundImage;
    }

    public static void show(Stage parentStage, AnimationTimer gameLoop) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(parentStage, gameLoop));
            return;
        }

        if (gameLoop != null) {
            gameLoop.stop();
        }

        Stage winStage = new Stage();
        winStage.setTitle("Win");
        winStage.initModality(Modality.APPLICATION_MODAL);
        winStage.initOwner(parentStage);
        winStage.setResizable(false);

        // === LAYER 1: HÌNH NỀN GIF ===
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

        // === LAYER 2: NỘI DUNG TRONG SUỐT ===
        VBox contentBox = new VBox(38);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent;");
        contentBox.setPrefSize(WIDTH, HEIGHT);

        // === NÚT CHO CONTINUE & EXIT ===
        Image greyBtnImage = loadImage("grey_button.png");
        if (greyBtnImage == null) {
            greyBtnImage = new Image("file:resources/grey_button.png");
        }

        Font btnFont = Font.font("Arial", 20);

        // === HIỆU ỨNG GLOW + SHADOW CHO TEXT (NHƯ BESTSCORE) ===
        String glowShadowStyle = "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
                "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        // === TIÊU ĐỀ: PAUSED ===
        Text titleText = new Text("YOU WIN");
        titleText.setFont(Font.font("Arial", 50));
        titleText.setFill(Color.rgb(206, 245, 129));
        titleText.setStyle(glowShadowStyle);
        VBox.setMargin(titleText, new Insets(0, 0, 50, 0));

        // === VOLUME CONTROLS – TRONG SUỐT, CÓ GLOW + SHADOW ===
        VBox volumeBox = new VBox(20);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setMaxWidth(360);
        volumeBox.setFillWidth(false);
        volumeBox.setStyle("-fx-background-color: transparent;");

        // Background Volume Label
        Text bgLabel = new Text("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        bgLabel.setFont(Font.font("Arial", 18));
        bgLabel.setFill(Color.rgb(206, 245, 129, 0.95));
        bgLabel.setStyle(glowShadowStyle);

        Slider backgroundSlider = new Slider(0, 100, VolumeManager.getBackgroundVolume() * 100);
        backgroundSlider.setMinWidth(280);
        backgroundSlider.setMaxWidth(280);
        backgroundSlider.setPrefWidth(280);
        backgroundSlider.setStyle(
                "-fx-pref-width: 280 !important; " +
                        "-fx-min-width: 280 !important; " +
                        "-fx-max-width: 280 !important; " +
                        "-fx-background-color: #666; " +
                        "-fx-background-radius: 14; " +
                        "-fx-padding: 10;"
        );

        backgroundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setBackgroundVolume(volume);
            bgLabel.setText("Background Volume: " + newVal.intValue() + "%");
        });

        // Effect Volume Label
        Text effectLabel = new Text("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 18));
        effectLabel.setFill(Color.rgb(206, 245, 129, 0.95));
        effectLabel.setStyle(glowShadowStyle);

        Slider effectSlider = new Slider(0, 100, VolumeManager.getEffectVolume() * 100);
        effectSlider.setMinWidth(280);
        effectSlider.setMaxWidth(280);
        effectSlider.setPrefWidth(280);
        effectSlider.setStyle(
                "-fx-pref-width: 280 !important; " +
                        "-fx-min-width: 280 !important; " +
                        "-fx-max-width: 280 !important; " +
                        "-fx-background-color: #666; " +
                        "-fx-background-radius: 14; " +
                        "-fx-padding: 10;"
        );

        effectSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + newVal.intValue() + "%");
        });

        volumeBox.getChildren().addAll(bgLabel, backgroundSlider, effectLabel, effectSlider);

        // === NÚT CONTINUE & EXIT ===
        VBox bottomBox = new VBox(20);
        bottomBox.setAlignment(Pos.CENTER);

        ImageButton againBtn = new ImageButton(greyBtnImage, "Play again", btnFont, mouseClickSound, 180);
        ImageButton exitBtn = new ImageButton(greyBtnImage, "Exit", btnFont, mouseClickSound, 180);

        // Thêm nút "Back to Menu"
        ImageButton backToMenuBtn = new ImageButton(greyBtnImage, "Back to Menu", btnFont, mouseClickSound, 180);

        againBtn.setOnAction(() -> {
            //stopMusicAndClose();
            winStage.close();
            MainGame.cleanup();
            MainGame.createAndShowGame(1); // Bắt đầu game mới từ level 1
        });

        backToMenuBtn.setOnAction(() -> {
            // 1. Dọn dẹp game hiện tại (dừng game loop, nhạc, video...)
            MainGame.cleanup();

            // 2. Đóng cửa sổ Pause
            winStage.close();

            // 3. Đóng cửa sổ MainGame (chính là parentStage)
            if (parentStage != null) {
                parentStage.close();
            }

            // 4. Hiển thị lại Menu chính
            // Phải chạy trên Platform.runLater để đảm bảo an toàn luồng FX
            Platform.runLater(() -> GameMenu.showMenu());
        });

        exitBtn.setOnAction(() -> {
            MainGame.exitMainGame();
            winStage.close();
            if (parentStage != null) parentStage.close();
            Platform.exit();
            System.exit(0);
        });

        // ===== SỬA DÒNG SAU =====
        HBox buttonBox = new HBox(40, againBtn, backToMenuBtn, exitBtn);
        // ===== KẾT THÚC SỬA =====

        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);

        // === GỘP TẤT CẢ ===
        contentBox.getChildren().addAll(titleText, volumeBox, bottomBox);
        stackRoot.getChildren().add(contentBox);

        // === HIỂN THỊ ===
        Scene scene = new Scene(stackRoot, WIDTH, HEIGHT);
        String css = Pause.class.getResource("/styles.css") != null ?
                Pause.class.getResource("/styles.css").toExternalForm() : "";
        if (!css.isEmpty()) scene.getStylesheets().add(css);

        winStage.setScene(scene);

        // ===== BẮT ĐẦU CODE THÊM (TRƯỚC ĐÓ) =====
        // Tích hợp nút X với nút Continue
        winStage.setOnCloseRequest(event -> {
            // Lấy logic y hệt như nút continueBtn
            if (gameLoop != null) gameLoop.start();
            Platform.runLater(() -> {
                MainGame.isPaused = false;
                parentStage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
                Paddle paddle = MainGame.staticPaddle;
                if (paddle != null) {
                    double screenX = parentStage.getX() + paddle.getX() + 8;
                    double screenY = parentStage.getY() + paddle.getY() + 50;
                    try {
                        java.awt.Robot robot = new java.awt.Robot();
                        robot.mouseMove((int) screenX, (int) screenY);
                    } catch (Exception ex) {
                        System.out.println("Không thể di chuyển chuột: " + ex.getMessage());
                    }
                }
            });
            // Không cần gọi winStage.close()
            // vì hành động mặc định của sự kiện này là đã tự đóng cửa sổ
        });
        // ===== KẾT THÚC CODE THÊM (TRƯỚC ĐÓ) =====

        winStage.showAndWait();
    }
}