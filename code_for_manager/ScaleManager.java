// ScaleManager.java
package code_for_manager;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
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
            return new Image("file:resources/" + name);
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
            return new Image("file:resources/" + name, true);
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

        // KIỂM TRA ĐẦU VÀO
        // 1. Kiểm tra ảnh gốc
        if (src.getWidth() <= 0 || src.getHeight() <= 0) {
            System.err.println("Không thể scale: Ảnh gốc có kích thước không hợp lệ.");
            return src; // Trả về ảnh gốc nếu không thể scale
        }

        // 2. Kiểm tra hệ số scale
        if (scale <= 0) {
            System.err.println("Không thể scale: Hệ số scale không hợp lệ (phải > 0): " + scale);
            return src; // Trả về ảnh gốc
        }

        // THAY ĐỔI CÁCH TÍNH TOÁN
        // Sử dụng Math.round() để làm tròn thay vì (int) để cắt cụt
        int w = (int) Math.round(src.getWidth() * scale);
        int h = (int) Math.round(src.getHeight() * scale);

        // ĐẢM BẢO KÍCH THƯỚC TỐI THIỂU
        // Đảm bảo w và h không bao giờ bằng 0 sau khi làm tròn
        w = Math.max(1, w); // Nếu w là 0, thì dùng 1
        h = Math.max(1, h); // Nếu h là 0, thì dùng 1

        // Dòng 50: Bây giờ đã an toàn
        WritableImage out = new WritableImage(w, h);
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = out.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Điều chỉnh lại logic lấy mẫu pixel một chút để mượt hơn
                int sx = (int) (x / (double) w * src.getWidth());
                int sy = (int) (y / (double) h * src.getHeight());

                // Đảm bảo không đọc ngoài lề ảnh gốc
                if (sx < 0) sx = 0;
                if (sy < 0) sy = 0;
                if (sx >= src.getWidth()) sx = (int) src.getWidth() - 1;
                if (sy >= src.getHeight()) sy = (int) src.getHeight() - 1;

                int argb = reader.getArgb(sx, sy);

                // Giữ nguyên độ trong suốt (logic này có vẻ không cần thiết,
                // vì bạn đang đọc cả alpha từ argb, nhưng tôi sẽ giữ lại)
                int alpha = (argb >> 24) & 0xFF;
                // int newAlpha = (int) (alpha * 1.0); // Dòng này không làm gì cả

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // writer.setArgb(x, y, (newAlpha << 24) | (r << 16) | (g << 8) | b);
                // Viết lại argb gốc là đủ
                writer.setArgb(x, y, argb);
            }
        }
        return out;
    }

    /**
     * Tải hình ảnh với kích thước mục tiêu (tự động scale)
     */
    public static Image loadAndScaleImage(String name, double targetWidth, double targetHeight) {
        Image original = loadImage(name);
        if (original == null) return null;

        double scaleX = targetWidth / original.getWidth();
        double scaleY = targetHeight / original.getHeight();
        double scale = Math.min(scaleX, scaleY);

        return createScaledImage(original, scale);
    }

    /**
     * Tải hình ảnh với chiều rộng mục tiêu (giữ tỷ lệ)
     */
    public static Image loadAndScaleImageByWidth(String name, double targetWidth) {
        Image original = loadImage(name);
        if (original == null) return null;

        double scale = targetWidth / original.getWidth();
        return createScaledImage(original, scale);
    }

    /**
     * Tải hình ảnh với chiều cao mục tiêu (giữ tỷ lệ)
     */
    public static Image loadAndScaleImageByHeight(String name, double targetHeight) {
        Image original = loadImage(name);
        if (original == null) return null;

        double scale = targetHeight / original.getHeight();
        return createScaledImage(original, scale);
    }
}