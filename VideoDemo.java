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

public class VideoDemo extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Đường dẫn tới file video (có thể là file mp4 trong thư mục project)
        String videoPath = getClass().getResource("VideoBackGround.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        Label titleLabel = new Label("ARKANOID");
        titleLabel.setTextFill(Color.CYAN);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));

        Button startButton = new Button("Start");
        startButton.setFont(Font.font("Arial", 18));
        startButton.setOnAction(event -> {
            System.out.println("Start button clicked!");
            // You can add additional actions here, e.g., start the game or close the video
        });

        VBox menuBox = new VBox(30, titleLabel, startButton);
        menuBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(mediaView, menuBox);
        Scene scene = new Scene(root, 1000, 800);

        primaryStage.setTitle("JavaFX Video Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
