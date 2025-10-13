import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Block extends GameObject {
    private Image image;
    private ImageView imageView;
    private static final String imagePath = "block.png";

    public Block(double x, double y, int width, int height) {
        super(x, y, width, height);
        image = new Image(imagePath);
        imageView = new ImageView(image);
        imageView.setX(getX());
        imageView.setY(getY());
        imageView.setFitWidth(getWidth());
        imageView.setFitHeight(getHeight());
    }

    // Hàm render: Đồng bộ thuộc tính từ GameObject sang Rectangle (nếu cần cập nhật động)
    @Override
    public void render() {
        /*imageView.setX(getX());
        imageView.setY(getY());
        imageView.setFitWidth(getWidth());
        imageView.setFitHeight(getHeight());*/
    }

    // Phương thức hỗ trợ: Trả về Node để thêm vào scene graph (Group hoặc Pane)
    public Node getNode() {
        return imageView;
    }
}