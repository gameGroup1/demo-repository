import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class Wall extends GameObject {
    private Rectangle rect;
    private Color color;

    public Wall(double x, double y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color; // Màu xám cho tường
        rect = new Rectangle(x, y, width, height);
    }

    // Hàm render: Đồng bộ thuộc tính từ GameObject sang Rectangle (nếu cần cập nhật động)
    @Override
    public void render() {
        rect.setX(getX());
        rect.setY(getY());
        rect.setWidth(getWidth());
        rect.setHeight(getHeight());
        rect.setFill(color);
    }

    // Phương thức hỗ trợ: Trả về Node để thêm vào scene graph (Group hoặc Pane)
    public Node getNode() {
        return rect;
    }
}