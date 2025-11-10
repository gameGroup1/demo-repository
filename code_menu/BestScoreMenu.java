package code_menu;

import code_button.*;
import code_def_path.*;
import code_manager.*;
import code_mainGame.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

public class BestScoreMenu {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 500;

    // Âm thanh click (giống GameMenu)
    private static final AudioClip mouseClickSound;

    static {
        mouseClickSound = new AudioClip(Path.getFileURL(Path.mouseClick));
        VolumeManager.registerAudioClip(mouseClickSound);
    }

    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Best Score");
        stage.setResizable(false);

        StackPane root = new StackPane();

        // === LOAD BACKGROUND ===
        Image bgImage = ScaleManager.loadImage(Path.bestScore.substring(1)); // "/resources/bestScoreMenu.png" → "resources/..."
        if (bgImage != null && !bgImage.isError()) {
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(WIDTH);
            background.setFitHeight(HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
            System.out.println("Loaded bestScoreMenu.png successfully");
            } else {
                root.setStyle("-fx-background-color: #1a1a1a;");
                System.err.println("Failed to load bestScoreMenu.png");
            }

        // === LẤY ĐIỂM CAO NHẤT ===
        int bestScore = MainGame.getBestScore();

        // === TIÊU ĐỀ ===
        Text titleText = new Text("BEST SCORE");
        titleText.setFont(Font.font("Arial", 36));
        titleText.setFill(Color.rgb(206, 245, 129));
        titleText.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 2, 2);");

        // === ĐIỂM SỐ ===
        Text scoreText = new Text(String.valueOf(bestScore));
        scoreText.setFont(Font.font("Arial", 30));
        scoreText.setFill(Color.GREEN);
        scoreText.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #FFD700, 15, 0.8, 0, 0);");

        // === LOAD NÚT XANH ===
        Image greenBtn = ScaleManager.loadImage(Path.greenButton.substring(1)); // "/resources/green_button.png"
        if (greenBtn == null || greenBtn.isError()) {
            System.err.println("Không load được green_button.png");
            // Fallback: dùng Button cũ
            javafx.scene.control.Button fallback = new javafx.scene.control.Button("EXIT TO MENU");
            fallback.setFont(Font.font("Arial", 24));
            fallback.setStyle("-fx-background-color: #a6f06d; -fx-text-fill: white; -fx-padding: 12 40; -fx-background-radius: 10;");
            fallback.setOnAction(e -> stage.close());

            VBox contentBox = new VBox(60, titleText, scoreText, fallback);
            contentBox.setAlignment(Pos.CENTER);
            root.getChildren().add(contentBox);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.show();
            return;
        }

        // === TẠO IMAGEBUTTON ===
        Font btnFont = Font.font("Arial", 27);
        ImageButton backButton = new ImageButton(greenBtn, "EXIT TO MENU", btnFont, mouseClickSound, 300);
        backButton.setOnAction(() -> stage.close());

        // === LAYOUT ===
        VBox contentBox = new VBox(60, titleText, scoreText, backButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent;");

        root.getChildren().add(contentBox);

        // === HIỂN THỊ ===
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}