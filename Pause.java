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

public class Pause {

    private static AudioClip mouseClickSound;
    public static final int WIDTH_PAUSE = 800;
    public static final int HEIGHT_PAUSE = 500;

    // CHỈNH ĐỘ TRONG SUỐT 
    private static final double TRACK_OPACITY = 0.75;  
    private static final double THUMB_OPACITY = 0.95;  // Nút kéo

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
            String[] paths = { "ImageGame/resources/PauseMenu.gif", "./resources/PauseMenu.gif", "../resources/PauseMenu.gif" };
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

        Stage pauseStage = new Stage();
        pauseStage.setTitle("Pause");
        pauseStage.initModality(Modality.APPLICATION_MODAL);
        pauseStage.initOwner(parentStage);
        pauseStage.setResizable(false);

        // === LAYER 1: HÌNH NỀN GIF ===
        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH_PAUSE);
            bgView.setFitHeight(HEIGHT_PAUSE);
            bgView.setPreserveRatio(false);
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;");
        }

        // === LAYER 2: NỘI DUNG ===
        VBox contentBox = new VBox(38);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent;");
        contentBox.setPrefSize(WIDTH_PAUSE, HEIGHT_PAUSE);

        Image greyBtnImage = loadImage("grey_button.png");
        if (greyBtnImage == null) {
            greyBtnImage = new Image("file:resources/grey_button.png");
        }

        Font btnFont = Font.font("Arial", 20);
        String glowShadowStyle = "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
            "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        // === TIÊU ĐỀ ===
        Text titleText = new Text("PAUSED");
        titleText.setFont(Font.font("Arial", 50));
        titleText.setFill(Color.rgb(206, 245, 129));
        titleText.setStyle(glowShadowStyle);
        VBox.setMargin(titleText, new Insets(0, 0, 50, 0));

        // === VOLUME CONTROLS ===
        VBox volumeBox = new VBox(20);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setMaxWidth(360);
        volumeBox.setStyle("-fx-background-color: transparent;");

        Text bgLabel = new Text("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        bgLabel.setFont(Font.font("Arial", 18));
        bgLabel.setFill(Color.rgb(206, 245, 129, 0.95));
        bgLabel.setStyle(glowShadowStyle);

        Slider backgroundSlider = new Slider(0, 100, VolumeManager.getBackgroundVolume() * 100);
        backgroundSlider.setMinWidth(280);
        backgroundSlider.setMaxWidth(280);
        backgroundSlider.setPrefWidth(280);
        backgroundSlider.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        Text effectLabel = new Text("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 18));
        effectLabel.setFill(Color.rgb(206, 245, 129, 0.95));
        effectLabel.setStyle(glowShadowStyle);

        Slider effectSlider = new Slider(0, 100, VolumeManager.getEffectVolume() * 100);
        effectSlider.setMinWidth(280);
        effectSlider.setMaxWidth(280);
        effectSlider.setPrefWidth(280);
        effectSlider.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        // === ÁP DỤNG TRONG SUỐT CHO SLIDER (SAU KHI HIỂN THỊ) ===
        Runnable applyTransparency = () -> {
            String trackStyle = String.format(
                "-fx-background-color: rgba(80, 80, 80, %.2f); " +
                "-fx-background-radius: 14; " +
                "-fx-pref-height: 10;",
                TRACK_OPACITY
            );

            String thumbStyle = String.format(
                "-fx-background-color: rgba(206, 245, 129, %.2f); " +
                "-fx-background-radius: 12; " +
                "-fx-pref-width: 22; " +
                "-fx-pref-height: 22; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0.6, 0, 1);",
                THUMB_OPACITY
            );

            // Áp dụng cho background slider
            var bgTrack = backgroundSlider.lookup(".track");
            var bgThumb = backgroundSlider.lookup(".thumb");
            if (bgTrack != null) bgTrack.setStyle(trackStyle);
            if (bgThumb != null) bgThumb.setStyle(thumbStyle);

            // Áp dụng cho effect slider
            var efTrack = effectSlider.lookup(".track");
            var efThumb = effectSlider.lookup(".thumb");
            if (efTrack != null) efTrack.setStyle(trackStyle);
            if (efThumb != null) efThumb.setStyle(thumbStyle);
        };

        // Chạy sau khi scene hiển thị
        pauseStage.setOnShown(e -> Platform.runLater(applyTransparency));

        // === SLIDER LISTENER ===
        backgroundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setBackgroundVolume(volume);
            bgLabel.setText("Background Volume: " + newVal.intValue() + "%");
        });

        effectSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            VolumeManager.setEffectVolume(volume);
            effectLabel.setText("Effect Volume: " + newVal.intValue() + "%");
        });

        volumeBox.getChildren().addAll(bgLabel, backgroundSlider, effectLabel, effectSlider);

        // === NÚT CONTINUE & EXIT ===
        VBox bottomBox = new VBox(20);
        bottomBox.setAlignment(Pos.CENTER);

        ImageButton continueBtn = new ImageButton(greyBtnImage, "Continue", btnFont, mouseClickSound, 180);
        ImageButton exitBtn = new ImageButton(greyBtnImage, "Exit", btnFont, mouseClickSound, 180);

        continueBtn.setOnAction(() -> {
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
            pauseStage.close();
        });

        exitBtn.setOnAction(() -> {
            MainGame.cleanup();
            pauseStage.close();
            if (parentStage != null) parentStage.close();
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(40, continueBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);

        // === GỘP TẤT CẢ ===
        contentBox.getChildren().addAll(titleText, volumeBox, bottomBox);
        stackRoot.getChildren().add(contentBox);

        // === HIỂN THỊ ===
        Scene scene = new Scene(stackRoot, WIDTH_PAUSE, HEIGHT_PAUSE);
        pauseStage.setScene(scene);
        pauseStage.showAndWait();
    }
}