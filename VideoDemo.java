import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoDemo extends Application {
    
    private Stage primaryStage;  // Lưu stage để truyền cho game
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;  // Lưu stage
        
        try {
            String videoPath = getClass().getResource("/VideoBackGround.mp4").toExternalForm();
            if (videoPath == null) {
                System.err.println("Không tìm thấy VideoBackGround.mp4!");
                return;
            }
            
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            
            // Loop video
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            Label titleLabel = new Label("ARKANOID");
            titleLabel.setTextFill(Color.CYAN);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));

            Button startButton = new Button("Start");
            startButton.setFont(Font.font("Arial", 18));
            startButton.setOnAction(event -> {
                System.out.println("Start button clicked!");
                mediaPlayer.stop();
                // Chuyển sang game: Tạo instance MainGame và gọi start với cùng stage
                //new MainGame().start(primaryStage);  // Đóng menu ngầm bằng cách thay scene
            });

            VBox menuBox = new VBox(25, titleLabel, startButton);
            menuBox.setAlignment(Pos.CENTER);

            StackPane root = new StackPane(mediaView, menuBox);
            Scene scene = new Scene(root, 1000, 400);

            primaryStage.setTitle("ARKANOID Menu");
            primaryStage.setScene(scene);
            primaryStage.show();

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}