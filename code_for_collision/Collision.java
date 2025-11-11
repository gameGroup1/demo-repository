package code_for_collision;

import code_def_path.*;
import code_for_button.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
import javafx.scene.media.AudioClip;

public class Collision {

    // Âm thanh khi bóng chạm tường
    public static final AudioClip ballWallSound;

    // Âm thanh khi bóng chạm thanh chắn (paddle)
    public static final AudioClip ballPaddleSound;

    // Âm thanh khi bóng chạm gạch (nhẹ - chưa vỡ)
    public static final AudioClip ballBrickSound;

    // Khối static: khởi tạo âm thanh ngay khi class được nạp
    static {
        ballWallSound = new AudioClip(Path.getFileURL(Path.ballWallSound));
        VolumeManager.registerAudioClip(ballWallSound); // Đăng ký để điều chỉnh âm lượng

        ballPaddleSound = new AudioClip(Path.getFileURL(Path.ballPaddleSound));
        VolumeManager.registerAudioClip(ballPaddleSound);

        ballBrickSound = new AudioClip(Path.getFileURL(Path.ballBrickSound));
        VolumeManager.registerAudioClip(ballBrickSound);
    }

    // Kiểm tra va chạm giữa bóng (hình tròn) và vật thể (hình chữ nhật)
    // Sử dụng phương pháp "closest point" + khoảng cách đến tâm
    public static boolean check(Ball ball, GameObject object) {
        if (ball == null || object == null) {
            return false; // Không có đối tượng → không va chạm
        }

        // Tìm điểm gần nhất trên hình chữ nhật với với tâm bóng
        double closestX = Math.max(object.getX(), Math.min(ball.getX(), object.getX() + object.getWidth()));
        double closestY = Math.max(object.getY(), Math.min(ball.getY(), object.getY() + object.getHeight()));

        // Tính khoảng cách từ tâm bóng đến điểm gần nhất
        double distanceX = ball.getX() - closestX;
        double distanceY = ball.getY() - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        // Nếu khoảng cách <= bán kính → va chạm
        if (distanceSquared <= ball.getRadius() * ball.getRadius()) {
            // Phát âm thanh theo loại vật thể
            if (object instanceof Paddle) {
                ballPaddleSound.play(VolumeManager.getEffectVolume()); // Chạm paddle
            } else if (object instanceof Wall) {
                ballWallSound.play(VolumeManager.getEffectVolume());   // Chạm tường
            }
            // Lưu ý: âm thanh chạm gạch được phát ở Update.position() khi gạch chưa vỡ
            return true;
        }
        return false;
    }

    // Kiểm tra va chạm giữa thanh chắn (paddle) và capsule (hình chữ nhật)
    // Sử dụng AABB collision (Axis-Aligned Bounding Box)
    public static boolean check(Paddle paddle, Capsule capsule) {
        if (paddle == null || capsule == null) {
            return false; // Không có đối tượng → không va chạm
        }

        // Tính biên phải và dưới của 2 hình chữ nhật
        double right1 = paddle.getX() + paddle.getWidth();
        double bottom1 = paddle.getY() + paddle.getHeight();
        double right2 = capsule.getX() + capsule.getWidth();
        double bottom2 = capsule.getY() + capsule.getHeight();

        // Kiểm tra 4 điều kiện không chồng lấp
        if (right1 < capsule.getX()) return false;     // Paddle bên trái capsule
        if (paddle.getX() > right2) return false;      // Paddle bên phải capsule
        if (bottom1 < capsule.getY()) return false;    // Paddle phía trên capsule
        if (paddle.getY() > bottom2) return false;     // Paddle phía dưới capsule

        // Nếu không rơi vào 4 trường hợp trên → có va chạm
        capsule.playSound(); // Phát âm thanh của capsule (ví dụ: "ting!")
        return true;
    }
}