package code_for_object;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_update.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import java.util.ArrayList;
import java.util.List;

public class BallTrailEffect {

    private final Node targetNode;
    private final Group root;
    private final List<ImageView> trails;
    private final int maxTrails;

    public BallTrailEffect(Node targetNode, Group root, int maxTrails) {
        this.targetNode = targetNode;
        this.root = root;
        this.maxTrails = maxTrails;
        this.trails = new ArrayList<>();
    }

    // Gọi trong mỗi frame update
    public void update(double centerX, double centerY) {
        if (targetNode.getScene() == null) return;

        // Chụp lại bóng hiện tại (PNG + alpha)
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        ImageView trail = new ImageView(targetNode.snapshot(params, null));

        double width = targetNode.getBoundsInParent().getWidth();
        double height = targetNode.getBoundsInParent().getHeight();
        trail.setFitWidth(width);
        trail.setFitHeight(height);
        trail.setX(centerX - width / 2);
        trail.setY(centerY - height / 2);
        trail.setOpacity(0.6);
        trail.setEffect(new GaussianBlur(2));
        // Thêm vệt mới nhất (ở cuối danh sách)
        root.getChildren().add(trail);
        trails.add(trail);

        // Giới hạn số lượng vệt
        if (trails.size() > maxTrails) {
            ImageView old = trails.remove(0);
            root.getChildren().remove(old);
        }

        // Cập nhật độ mờ cho từng vệt theo khoảng cách
        for (int i = 0; i < trails.size(); i++) {
            ImageView t = trails.get(i);
            double progress = (double) i / trails.size(); //khoảng cách giữa các vệt
            double opacity = Math.pow(progress,3) * 0.6; // chỉnh độ mờ
            double blur = (1.0 - progress) * 8; // chỉnh độ nhạt

            t.setOpacity(opacity);
            t.setEffect(new GaussianBlur(blur));
        }
    }

    public void clear() {
        for (ImageView trail : trails) {
            root.getChildren().remove(trail);
        }
        trails.clear();
    }

    // Constructor rút gọn
    public BallTrailEffect(Node targetNode, Group root) {
        this(targetNode, root, 10);
    }
}
