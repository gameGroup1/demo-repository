/* Lớp đại diện cho tấm ván */
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class Paddle extends GameObject {
    private Rectangle rect;

    public Paddle(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material);
        rect = new Rectangle(x, y, width, height);
        rect.setFill(material.getColor());
    }

    // Hàm move: Di chuyển paddle theo chuột, kết hợp với giới hạn biên từ Wall
    public void move(MouseEvent event, Wall leftWall, Wall rightWall) {
        if (event == null || leftWall == null || rightWall == null) {
            return; // Xử lý lỗi: Tránh NullPointerException (phần 4.1.1)
        }

        double mouseX = event.getX(); // Lấy tọa độ X của chuột
        double newX = mouseX; // Căn giữa Paddle với chuột

        // Tính biên giới từ leftWall và rightWall (đóng gói logic biên)
        double leftBound = leftWall.getX() + leftWall.getWidth(); // Biên trái sau tường
        double rightBound = rightWall.getX(); // Biên phải trước tường

        // Giới hạn newX để Paddle không vượt biên (clamp thủ công, thay Update.position)
        if (newX < leftBound) {
            newX = leftBound;
        } else if (newX + getWidth() > rightBound) {
            newX = rightBound - getWidth();
        }

        super.setX(newX); // Cập nhật vị trí từ GameObject (kế thừa phần 5.1)
        rect.setX(newX);  // Đồng bộ với Rectangle trong JavaFX scene graph (phần 4.2.1)

        // Không cần gọi Update.position(this, wall) nữa, vì đã clamp thủ công
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