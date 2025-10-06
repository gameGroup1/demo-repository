/* Lớp đại diện cho khối gạch trong game Arkanoid (kế thừa từ GameObject) */
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

public class Bricks extends GameObject {
    public boolean isBreak = false; // Trạng thái gạch: phá hủy hay chưa (private, đóng gói)
    private Rectangle rect; // Đối tượng JavaFX để hiển thị (tích hợp GUI - phần 4.2.1)

    /**
     * Constructor: Khởi tạo gạch với vị trí, kích thước và material.
     * @param x Vị trí X ban đầu
     * @param y Vị trí Y ban đầu
     * @param width Chiều rộng gạch
     * @param height Chiều cao gạch
     * @param material Vật liệu (màu sắc, độ cứng, mật độ - phần 5.1)
     */
    public Bricks(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material); // Kế thừa từ GameObject
        this.rect = new Rectangle(x, y, width, height); // Tạo Rectangle
        if (material != null) {
            rect.setFill(material.getColor()); // Gán màu từ Material
        }
    }

    /**
     * Hàm render: Đồng bộ thuộc tính để hiển thị (gọi trong game loop sau va chạm).
     * Áp dụng đa hình: Override từ GameObject để vẽ Rectangle riêng, ẩn nếu isBreak = true.
     */
    @Override
    public void render() {
        if (rect != null) {
            if (!isBreak) {
                // Đồng bộ vị trí, kích thước, màu từ GameObject/Material
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

    // Getter và setter cho isBreak (sử dụng trong Collision/Update để kiểm tra va chạm - phần 4.1.1)
    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    /**
     * Phương thức xử lý khi gạch bị va chạm (gọi từ Update.position(ball, bricks)).
     * Áp dụng trừu tượng hóa: Ẩn chi tiết phá gạch (có thể mở rộng cho StrongBrick - phần 4.3.1).
     */
    public void takeHit() {
        this.isBreak = true; // Đánh dấu gạch bị phá (có thể thêm logic Power-up ở đây)
    }

    // Phương thức hỗ trợ: Trả về Node để thêm vào scene graph (Group hoặc Pane - phần 4.2.1)
    public Node getNode() {
        return rect;
    }
}