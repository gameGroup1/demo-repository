// SettingMenu.java
import javafx.application.Platform;
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
import javafx.scene.media.AudioClip;

public class SettingMenu {
    private static Stage settingsStage;
    private static AudioClip mouseClickSound;

    // === ĐỒNG NHẤT VỚI PAUSE.JAVA ===
    private static final double TRACK_OPACITY = 0.75;  
    private static final double THUMB_OPACITY = 0.95;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    public static void show(Stage owner) {
        settingsStage = new Stage();
        if (owner != null) {
            settingsStage.initOwner(owner);
        }
        settingsStage.setTitle("Settings");
        settingsStage.setResizable(false);

        // Root container với background
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

        VBox content = new VBox(38); // Khoảng cách giống Pause
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setMaxWidth(600);

        // === TIÊU ĐỀ GIỐNG PAUSE ===
        javafx.scene.text.Text title = new javafx.scene.text.Text("SETTINGS");
        title.setFont(Font.font("Arial", 50));
        title.setFill(Color.rgb(206, 245, 129));
        String glowShadowStyle = "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
            "dropshadow(gaussian, black, 10, 0.5, 2, 2);";
        title.setStyle(glowShadowStyle);
        VBox.setMargin(title, new Insets(0, 0, 50, 0));

        // === VOLUME CONTROLS ===
        VBox volumeBox = new VBox(20);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setMaxWidth(360);

        Label bgLabel = new Label("Background Volume: " + (int)(VolumeManager.getBackgroundVolume() * 100) + "%");
        bgLabel.setFont(Font.font("Arial", 18));
        bgLabel.setTextFill(Color.rgb(206, 245, 129, 0.95));
        bgLabel.setStyle(glowShadowStyle);

        Slider backgroundSlider = new Slider(0, 100, VolumeManager.getBackgroundVolume() * 100);
        backgroundSlider.setMinWidth(280);
        backgroundSlider.setMaxWidth(280);
        backgroundSlider.setPrefWidth(280);
        backgroundSlider.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        Label effectLabel = new Label("Effect Volume: " + (int)(VolumeManager.getEffectVolume() * 100) + "%");
        effectLabel.setFont(Font.font("Arial", 18));
        effectLabel.setTextFill(Color.rgb(206, 245, 129, 0.95));
        effectLabel.setStyle(glowShadowStyle);

        Slider effectSlider = new Slider(0, 100, VolumeManager.getEffectVolume() * 100);
        effectSlider.setMinWidth(280);
        effectSlider.setMaxWidth(280);
        effectSlider.setPrefWidth(280);
        effectSlider.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        // === ÁP DỤNG TRONG SUỐT CHO SLIDER ===
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

            // Background slider
            var bgTrack = backgroundSlider.lookup(".track");
            var bgThumb = backgroundSlider.lookup(".thumb");
            if (bgTrack != null) bgTrack.setStyle(trackStyle);
            if (bgThumb != null) bgThumb.setStyle(thumbStyle);

            // Effect slider
            var efTrack = effectSlider.lookup(".track");
            var efThumb = effectSlider.lookup(".thumb");
            if (efTrack != null) efTrack.setStyle(trackStyle);
            if (efThumb != null) efThumb.setStyle(thumbStyle);
        };

        settingsStage.setOnShown(e -> Platform.runLater(applyTransparency));

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

        // === NÚT BACK TO MENU ===
        Image greyBtnNormal = loadImage("grey_button.png");
        if (greyBtnNormal == null) {
            System.err.println("Không tải được grey_button.png");
            greyBtnNormal = new Image("file:ImageGame/resources/grey_button.png");
        }

        Font btnFont = Font.font("Arial", 20);
        ImageButton backBtn = new ImageButton(greyBtnNormal, "BACK TO MENU", btnFont, mouseClickSound, 300);
        backBtn.setOnAction(() -> settingsStage.close());

        VBox buttonBox = new VBox(20, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // === GỘP TẤT CẢ ===
        content.getChildren().addAll(title, volumeBox, buttonBox);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 1100, 500);
        settingsStage.setScene(scene);
        settingsStage.showAndWait();
    }

    private static Image loadBackground() {
        return ScaleManager.loadAnimatedImage("SettingMenu.gif");
    }

    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }
}