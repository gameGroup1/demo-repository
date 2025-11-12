package code_for_menu;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_object.*;
import code_for_update.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WinMenu {
    private static Stage stage;
    public static final int WIDTH_END = 1000;
    public static final int HEIGHT_END = 400;

    public static void show() {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> WinMenu.show());
            return;
        }
        stage = new Stage();
        stage.setTitle("Victory!");
        stage.setResizable(false);

        StackPane stackRoot = new StackPane();
        Image backgroundImage = loadBackgroundImage();
        if (backgroundImage != null) {
            ImageView bgView = new ImageView(backgroundImage);
            bgView.setFitWidth(WIDTH_END);
            bgView.setFitHeight(HEIGHT_END);
            bgView.setPreserveRatio(false);
            stackRoot.getChildren().add(bgView);
        } else {
            stackRoot.setStyle("-fx-background-color: #2b2b2b;");
        }

        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");

        String glowShadowStyle = "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, #565c4cff, 10, 0.8, 0, 0), " +
                "dropshadow(gaussian, black, 10, 0.5, 2, 2);";

        Text titleText = new Text("You Win");
        titleText.setFont(Font.font("Arial", 72));
        titleText.setFill(Color.rgb(206, 245, 129));
        titleText.setStyle(glowShadowStyle);

        BorderPane.setAlignment(titleText, Pos.CENTER);
        contentPane.setTop(titleText);
        BorderPane.setMargin(titleText, new Insets(20, 0, 0, 0));

        VBox centerBox = new VBox(18);
        centerBox.setAlignment(Pos.CENTER);

        contentPane.setCenter(centerBox);

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 15; -fx-background-color: transparent;");

        Image greyBtn = loadImage("grey_button.png");
        Font btnFont = Font.font("Arial", 20);

        ImageButton exitBtn = new ImageButton(greyBtn, "Exit", btnFont, null, 180);
        ImageButton backToMenuBtn = new ImageButton(greyBtn, "Back to Menu", btnFont, null, 190);

        backToMenuBtn.setOnAction(() -> {
            if (stage != null) stage.close();
            Platform.runLater(() -> GameMenu.showMenu());
        });

        exitBtn.setOnAction(() -> {
            if (stage != null) stage.close();
            Platform.exit();
            System.exit(0);
        });

        HBox buttonBox = new HBox(15, backToMenuBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(buttonBox);
        contentPane.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        stackRoot.getChildren().add(contentPane);
        Scene scene = new Scene(stackRoot, WIDTH_END, HEIGHT_END);
        stage.setScene(scene);
        stage.setWidth(WIDTH_END);
        stage.setHeight(HEIGHT_END);

        stage.show();
    }

    private static Image loadBackgroundImage() {
        return ScaleManager.loadAnimatedImage("Victory.gif");
    }

    private static Image loadImage(String name) {
        return ScaleManager.loadImage(name);
    }
}