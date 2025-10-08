/* Lớp đại diện cho khối gạch trong game Arkanoid (kế thừa từ GameObject) */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.geometry.Rectangle2D; // Import để sử dụng viewport cho sprite sheet
import java.util.List;
import java.util.ArrayList;

public class Bricks extends GameObject {
    private ImageView imageView; // Đối tượng JavaFX để hiển thị hình ảnh
    private Image spriteSheet; // Ảnh sprite sheet duy nhất chứa tất cả frames
    private List<Rectangle2D> frameViewport; // Danh sách viewport (tọa độ và kích thước) của từng frame, tương ứng với các mức health
    private int frameIndex = 0; // Chỉ số frame hiện tại dựa trên health

    public Bricks(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material); // Kế thừa từ GameObject
        loadFrameAndSheet(); // Khởi tạo sprite sheet và frame viewport dựa trên vật liệu
        imageView = new ImageView(spriteSheet);
        updateFrame(); // Cập nhật frame ban đầu dựa trên health
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    private void loadFrameAndSheet(){
        switch (getMaterial()){
            case metal:
                spriteSheet = Frames.jewelSprite;
                frameViewport = Frames.metalBrick;
                break;
            case rock:
                spriteSheet = Frames.rockSprite;
                frameViewport = Frames.rockBrick;
                break;
            case wood:
                spriteSheet = Frames.woodSprite;
                frameViewport = Frames.woodBrick;
                break;
            default:
                spriteSheet = Frames.jewelSprite;
                frameViewport = Frames.jewelBrick;
                break;
        }
    }

    private void updateFrame() {
        if (!isBreak()) {
            imageView.setViewport(frameViewport.get(frameIndex)); // Cập nhật viewport
        }
    }

    @Override
    public void render() {
        if (imageView != null) {
            if (!isBreak()) {
                imageView.setVisible(true); // Hiển thị nếu chưa phá
            } else {
                imageView.setVisible(false); // Ẩn gạch nếu đã phá (phần 4.1.1: phá hủy Brick)
            }
        }
    }

    public boolean isBreak() {
        return frameIndex >= frameViewport.size();
    }

    public void takeHit(int power) {
        frameIndex += power;
        updateFrame();
    }

    public Node getNode() {
        return imageView;
    }
}