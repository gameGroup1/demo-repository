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
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

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

        // Tiêu đề Game Over với hiệu ứng co giãn liên tục
        Label titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.RED);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // Áp dụng hiệu ứng co giãn liên tục cho titleLabel
     /*    ScaleTransition titleScale = new ScaleTransition(Duration.seconds(1.5), titleLabel);
        titleScale.setFromX(0.8);
        titleScale.setFromY(0.8);
        titleScale.setToX(1.2);
        titleScale.setToY(1.2);
        titleScale.setCycleCount(ScaleTransition.INDEFINITE);
        titleScale.setAutoReverse(true);
        titleScale.play();*/

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

        // Phần dưới - Các nút với hiệu ứng hover
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-border-color: #555; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #3b3b3b;");

        Button playAgainBtn = new Button("Play Again");
        Button exitBtn = new Button("Exit");

        playAgainBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        exitBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #f44336; -fx-text-fill: white;");

        // Hiệu ứng scale khi hover cho playAgainBtn
        playAgainBtn.setOnMouseEntered(e -> {
            playAgainBtn.setScaleX(1.1);
            playAgainBtn.setScaleY(1.1);
        });
        playAgainBtn.setOnMouseExited(e -> {
            playAgainBtn.setScaleX(1.0);
            playAgainBtn.setScaleY(1.0);
        });

        // Hiệu ứng scale khi hover cho exitBtn
        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setScaleX(1.1);
            exitBtn.setScaleY(1.1);
        });
        exitBtn.setOnMouseExited(e -> {
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        // Hiệu ứng scale khi focus (bàn phím) cho playAgainBtn
        playAgainBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                playAgainBtn.setScaleX(1.1);
                playAgainBtn.setScaleY(1.1);
            } else {
                playAgainBtn.setScaleX(1.0);
                playAgainBtn.setScaleY(1.0);
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