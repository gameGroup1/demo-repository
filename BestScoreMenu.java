import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BestScoreMenu {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 600;

    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Best Score");
        stage.setResizable(false);

        StackPane root = new StackPane();

        // Load background
        //Image bgImage = ScaleManager.loadImage(Path.bestScore);
        Image bgImage = new Image("file:resources/bestScoreBG.png");
        

        if (bgImage != null) {
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(WIDTH);
            background.setFitHeight(HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
            System.out.println("Loaded bestScoreBG.png successfully");
        } else {
            root.setStyle("-fx-background-color: #1a1a1a;");
            System.err.println("Failed to load bestScoreBG.png");
        }

        // Lấy điểm cao nhất từ MainGame
        int bestScore = MainGame.getBestScore();

        // Tiêu đề
        Text titleText = new Text("BEST SCORE");
        titleText.setFont(Font.font("Arial", 36));
        titleText.setFill(Color.rgb(206, 245, 129));
        titleText.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 2, 2);");

        // Điểm số
        Text scoreText = new Text(String.valueOf(bestScore));
        scoreText.setFont(Font.font("Arial", 30));
        scoreText.setFill(Color.GREEN);
        scoreText.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #FFD700, 15, 0.8, 0, 0);");

        // Nút Back to Menu
        Button backButton = new Button("BACK TO MENU");
        backButton.setFont(Font.font("Arial", 24));
        backButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4a90e2, #2c5aa0); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 40; " +
            "-fx-background-radius: 10; " +
            "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, black, 8, 0.5, 0, 2);"
        );

        // Hover effect
        backButton.setOnMouseEntered(e -> {
        backButton.setScaleX(1.05);
        backButton.setScaleY(1.05);
        });

        backButton.setOnMouseExited(e -> {
        backButton.setScaleX(1.0);
        backButton.setScaleY(1.0);
        });

        // Hành động khi nhấn nút
        backButton.setOnAction(e -> {
            stage.close();
        });

        // Layout nội dung
        VBox contentBox = new VBox(60, titleText, scoreText, backButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: transparent;");

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}