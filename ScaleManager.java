import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import java.util.ArrayList;
import java.util.List;

public class ScaleManager {
    private static double scale = 1.0; // Scale thống nhất mặc định

    // Danh sách các node có thể scale (bao gồm root/background)
    private static List<Node> scalableNodes = new ArrayList<>();

    // Constructor: Đăng ký các nodes ban đầu
    public static void registerScalableNode(Node node) {
        scalableNodes.add(node);
    }

    public static double getScale() {
        return scale;
    }

    public static void setScale(double s) {
        if (s < 0.5) s = 0.5;
        else if (s > 2.0) s = 2.0;
        scale = s;
        applyScalesToAll();
    }

    // Áp dụng scale cho tất cả nodes đã đăng ký
    private static void applyScalesToAll() {
        for (Node node : scalableNodes) {
            if (node != null) {
                applyScaleToNode(node);
            }
        }
    }

    // Áp dụng scale cho một node (hỗ trợ cả ImageView và Rectangle)
    public static void applyScaleToNode(Node node) {
        if (node instanceof ImageView) {
            ImageView imageView = (ImageView) node;
            if (imageView.getImage() != null) {
                imageView.setFitWidth(scale * imageView.getImage().getWidth());
                imageView.setFitHeight(scale * imageView.getImage().getHeight());
            }
            imageView.setX(scale * imageView.getX());
            imageView.setY(scale * imageView.getY());
        } else if (node instanceof Rectangle) {
            Rectangle rect = (Rectangle) node;
            rect.setWidth(scale * rect.getWidth());
            rect.setHeight(scale * rect.getHeight());
            rect.setX(scale * rect.getX());
            rect.setY(scale * rect.getY());
        } else if (node instanceof Group) {
            // Scale toàn bộ group
            Scale transform = new Scale(scale, scale);
            node.getTransforms().clear();
            node.getTransforms().add(transform);
        }
        // Có thể mở rộng cho các loại Node khác nếu cần
    }

    // Áp dụng scale transform cho root node (toàn bộ scene)
    public static void applyScaleToRoot(Node root) {
        if (root != null) {
            root.getTransforms().clear();
            root.getTransforms().add(new Scale(scale, scale));
        }
    }
}