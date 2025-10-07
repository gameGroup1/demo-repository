import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class EndMenu {
    private static Stage stage;
    private static int score;
    private static int bestScore;

    public static void show(int score, int bestScore) {
        EndMenu.score = score;
        EndMenu.bestScore = bestScore;

        // Kiểm tra xem JavaFX đã được khởi tạo chưa
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(score, bestScore));
            return;
        }
        
        stage = new Stage();
        stage.setTitle("Game Over");
        stage.setResizable(false);

        // Tạo layout chính
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20;");

        // Tiêu đề Game Over
        Label titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.RED);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // Phần giữa - Hiển thị điểm
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font("Arial", 24));
        scoreLabel.setTextFill(Color.WHITE);
        
        Label bestScoreLabel = new Label("Best Score: " + bestScore);
        bestScoreLabel.setFont(Font.font("Arial", 24));
        bestScoreLabel.setTextFill(Color.YELLOW);

        centerBox.getChildren().addAll(scoreLabel, bestScoreLabel);
        root.setCenter(centerBox);

        // Phần dưới - Các nút
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-border-color: #555; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #3b3b3b;");

        Button playAgainBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        playAgainBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35;");
        exitBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35;");

        playAgainBtn.setOnAction(e -> {
            stage.close();
            startNewGame();
        });

        exitBtn.setOnAction(e -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playAgainBtn, exitBtn);
        
        bottomBox.getChildren().add(buttonBox);
        root.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private static void startNewGame() {
        // Không cần sleep nữa, trực tiếp khởi chạy game mới
        MainGame.createAndShowGame();
    }
}