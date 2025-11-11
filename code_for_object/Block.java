package code_for_object;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_update.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.BoxBlur;
public class Block extends GameObject {
    private Image image;
    private ImageView imageView;
    private static final String imagePath = "file:resources/block.png";
    private BoxBlur blurEffect;

    public Block(double x, double y, int width, int height) {
        super(x, y, width, height);
        image = new Image(imagePath);
        imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        blurEffect = new BoxBlur(width,height,1);
    }

    // Hàm render: Đồng bộ thuộc tính từ GameObject sang Rectangle (nếu cần cập nhật động)
    @Override
    public void render() {}

    // Phương thức hỗ trợ: Trả về Node để thêm vào scene graph (Group hoặc Pane)
    public Node getNode() {
        return imageView;
    }
}