import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Pause {

    public static void show(Stage parentStage, AnimationTimer gameLoop) {
        // Kiểm tra xem JavaFX đã được khởi tạo chưa
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
        pauseStage.initModality(Modality.APPLICATION_MODAL); // Làm modal để block input cho parent
        pauseStage.initOwner(parentStage);
        pauseStage.setResizable(false);

        // Tạo layout chính
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20;");

        // Tiêu đề Pause
        Label titleLabel = new Label("PAUSED");
        titleLabel.setFont(Font.font("Arial", 36));
        titleLabel.setTextFill(Color.CYAN);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // Phần dưới - Các nút
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: #3b3b3b;");

        Button continueBtn = new Button("Continue");
        Button exitBtn = new Button("Exit");

        continueBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        exitBtn.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 35; -fx-background-color: #f44336; -fx-text-fill: white;");

        // Hiệu ứng scale khi hover cho continueBtn
        continueBtn.setOnMouseEntered(e -> {
            continueBtn.setScaleX(1.1);
            continueBtn.setScaleY(1.1);
        });
        continueBtn.setOnMouseExited(e -> {
            continueBtn.setScaleX(1.0);
            continueBtn.setScaleY(1.0);
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

        // Hiệu ứng scale khi focus (bàn phím) cho continueBtn
        continueBtn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                continueBtn.setScaleX(1.1);
                continueBtn.setScaleY(1.1);
            } else {
                continueBtn.setScaleX(1.0);
                continueBtn.setScaleY(1.0);
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

        // Action cho Continue: Tiếp tục game
        continueBtn.setOnAction(e -> {
            if (gameLoop != null) {
                gameLoop.start();
            }
            pauseStage.close();
        });

        // Action cho Exit: Thoát game
        exitBtn.setOnAction(e -> {
            pauseStage.close();
            parentStage.close();
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(continueBtn, exitBtn);
        
        bottomBox.getChildren().add(buttonBox);
        root.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(root, 300, 200); // Kích thước nhỏ hơn cho menu pause
        pauseStage.setScene(scene);
        pauseStage.showAndWait(); // Chờ đến khi close mới tiếp tục
    }
}