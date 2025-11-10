// ScaleManager.java
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.net.URL;

public class ScaleManager {
    
    /**
     * Tải hình ảnh từ resources với fallback
     */
    public static Image loadImage(String name) {
        try {
            URL url = ScaleManager.class.getClassLoader().getResource(name);
            if (url != null) {
                return new Image(url.toString());
            }
            return new Image("file:ImageGame/resources/" + name);
        } catch (Exception e) {
            System.err.println("Không tải được hình ảnh: " + name);
            return null;
        }
    }
    
    /**
     * Tải hình ảnh động (GIF) từ resources với fallback
     */
    public static Image loadAnimatedImage(String name) {
        try {
            URL url = ScaleManager.class.getClassLoader().getResource(name);
            if (url != null) {
                return new Image(url.toString(), true);
            }
            return new Image("file:ImageGame/resources/" + name, true);
        } catch (Exception e) {
            System.err.println("Không tải được hình ảnh động: " + name);
            return null;
        }
    }
    
    /**
     * Tạo hình ảnh scaled từ hình gốc
     */
    public static Image createScaledImage(Image src, double scale) {
        if (src == null) return null;
        
        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);
        WritableImage out = new WritableImage(w, h);
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = out.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int sx = (int) (x / scale);
                int sy = (int) (y / scale);
                
                if (sx < src.getWidth() && sy < src.getHeight()) {
                    int argb = reader.getArgb(sx, sy);
                    
                    // Giữ nguyên độ trong suốt
                    int alpha = (argb >> 24) & 0xFF;     
                    int newAlpha = (int) (alpha * 1.0); 
        
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;

                    writer.setArgb(x, y, (newAlpha << 24) | (r << 16) | (g << 8) | b);
                }
            }
        }
        return out;
    }
}