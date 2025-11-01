// ImageButtonFX.java
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

public class ImageButton extends StackPane {
    private final ImageView normalView;
    private final ImageView hoveredView;
    private final Text textNode;
    private final ScaleTransition scaleIn;
    private final ScaleTransition scaleOut;
    private final AudioClip hoverSound;

    public ImageButton(Image normalImage, String text, Font font, AudioClip hoverSound, double targetWidth) {
        this.hoverSound = hoverSound;

        // Tính tỷ lệ ảnh gốc
        double aspectRatio = normalImage.getWidth() / normalImage.getHeight();
        double targetHeight = targetWidth / aspectRatio;
        setMaxSize(targetWidth, targetHeight);
        setPrefSize(targetWidth, targetHeight);
        // Normal image
        normalView = new ImageView(normalImage);
        normalView.setFitWidth(targetWidth);
        normalView.setFitHeight(targetHeight);
        normalView.setSmooth(true);

        // Hovered image: scale 1.2x từ normal
        Image hoveredImage = createScaledImage(normalImage, 1.2);
        hoveredView = new ImageView(hoveredImage);
        hoveredView.setFitWidth(targetWidth * 1.05);
        hoveredView.setFitHeight(targetHeight * 1.05);
        hoveredView.setSmooth(true);
        hoveredView.setVisible(false);

        // Text
        textNode = new Text(text);
        textNode.setFont(font);
        textNode.setFill(Color.web("#ecfacaff"));
        textNode.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 3, 0, 1, 1);");

        getChildren().addAll(normalView, hoveredView, textNode);
        setAlignment(javafx.geometry.Pos.CENTER);
     
        normalView.setOpacity(0.7);   // ← Ảnh nền mờ 15%
        hoveredView.setOpacity(0.7);   // ← Ảnh hover: đục 100% (nổi bật)
        // Chữ luôn đục 100%
        textNode.setOpacity(1.0);      // ← Đảm bảo chữ không bị ảnh hưởng
    
        // Animation
        scaleIn = new ScaleTransition(Duration.millis(180), this);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        scaleOut = new ScaleTransition(Duration.millis(180), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        // Hover
        setOnMouseEntered(e -> {
            normalView.setVisible(false);
            hoveredView.setVisible(true);
            scaleIn.playFromStart();
            if (hoverSound != null) hoverSound.play(VolumeManager.getEffectVolume());
        });

        setOnMouseExited(e -> {
            normalView.setVisible(true);
            hoveredView.setVisible(false);
            scaleOut.playFromStart();
        });
    }

    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    }

    // Tạo ảnh scale
    private Image createScaledImage(Image src, double scale) {
        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);
        WritableImage out = new WritableImage(w, h);
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = out.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int sx = (int) (x / scale);
                int sy = (int) (y / scale);
                int argb = reader.getArgb(sx, sy);

                //CHỈNH ĐỘ TRONG SUỐT TẠI ĐÂY
                int alpha = (argb >> 24) & 0xFF;     
                int newAlpha = (int) (alpha * 1); 
    
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                writer.setArgb(x, y, (newAlpha << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return out;
    }
}