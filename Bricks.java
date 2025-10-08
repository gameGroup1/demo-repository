/* Lớp đại diện cho khối gạch trong game Arkanoid (kế thừa từ GameObject) */
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

public class Bricks extends GameObject {
    private Rectangle rect; // Đối tượng JavaFX để hiển thị (tích hợp GUI - phần 4.2.1)
    private int health;

    public Bricks(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material); // Kế thừa từ GameObject
        rect = new Rectangle(x, y, width, height); // Tạo Rectangle
        rect.setFill(material.getColor()); // Gán màu từ Material
        health = (int) material.getHardness(); // Sử dụng độ cứng làm máu
    }

    @Override
    public void render() {
        if (rect != null) {
            if (!isBreak()) {
                rect.setX(getX());
                rect.setY(getY());
                rect.setWidth(getWidth());
                rect.setHeight(getHeight());
                if (getMaterial() != null) {
                    rect.setFill(getMaterial().getColor()); // Cập nhật màu (hiệu ứng hình ảnh - phần 4.2.2)
                }
                rect.setVisible(true); // Hiển thị nếu chưa phá
            } else {
                rect.setVisible(false); // Ẩn gạch nếu đã phá (phần 4.1.1: phá hủy Brick)
            }
        }
    }

    public boolean isBreak() {
        return health <= 0;
    }

    public void takeHit(int power) {
        health = health - power;
    }

    public Node getNode() {
        return rect;
    }
}