import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

public class Wall extends GameObject {
    private Rectangle rect;

    public Wall(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material);
        rect = new Rectangle(x, y, width, height);
        rect.setFill(material.getColor());
    }

    // Hàm render: Đồng bộ thuộc tính từ GameObject sang Rectangle (nếu cần cập nhật động)
    @Override
    public void render() {
        rect.setX(getX());
        rect.setY(getY());
        rect.setWidth(getWidth());
        rect.setHeight(getHeight());
        if (getMaterial() != null) {
            rect.setFill(getMaterial().getColor()); // Cập nhật màu nếu material thay đổi
        }
    }

    // Phương thức hỗ trợ: Trả về Node để thêm vào scene graph (Group hoặc Pane)
    public Node getNode() {
        return rect;
    }
}