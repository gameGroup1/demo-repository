import javafx.scene.media.AudioClip;

public class Update {

    // Âm thanh khi mất mạng (bóng rơi xuống đáy)
    public static final AudioClip loseLifeSound;

    // Âm thanh khi phá gạch hoàn toàn
    public static final AudioClip brickBreakSound;

    // Khối static: khởi tạo âm thanh ngay khi class được nạp
    static {
        loseLifeSound = new AudioClip(Path.getFileURL(Path.loseLifeSound));
        VolumeManager.registerAudioClip(loseLifeSound); // Đăng ký để điều chỉnh âm lượng chung

        brickBreakSound = new AudioClip(Path.getFileURL(Path.brickBreakSound));
        VolumeManager.registerAudioClip(brickBreakSound);
    }

    // Cập nhật vị trí bóng theo vận tốc (dx, dy)
    public static void position(Ball ball) {
        if (ball == null) return;
        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());
    }

    // Cập nhật vị trí capsule rơi xuống (chỉ theo trục Y)
    public static void position(Capsule capsule) {
        if (capsule == null) return;
        capsule.setY(capsule.getY() + capsule.getSpeed());
    }

    // Xử lý va chạm giữa bóng và thanh chắn (paddle)
    // Thay đổi hướng bóng theo vị trí va chạm + hiệu ứng "góc nảy"
    public static void position(Ball ball, Paddle paddle) {
        if (!Collision.check(ball, paddle)) return;

        // Đảo chiều Y (nảy lên)
        ball.setDy(-ball.getDy());

        // Tính vị trí va chạm tương đối so với giữa paddle: -1 (trái) đến +1 (phải)
        double colPosition = ((ball.getX() - paddle.getX()) - (paddle.getWidth() / 2.0)) / (paddle.getWidth() / 2.0);

        // Giới hạn giá trị trong khoảng [-1, 1]
        if (colPosition < -1) colPosition = -1;
        if (colPosition > 1) colPosition = 1;

        // Góc nảy tối đa: 60 độ (PI/3)
        double maxAngle = Math.PI / 3;
        double angle = colPosition * maxAngle;

        // Tính toán vận tốc mới theo góc
        double speed = ball.getSpeed();
        ball.setDx(speed * Math.sin(angle));
        ball.setDy(-speed * Math.cos(angle)); // Âm để bóng bay lên
    }

    // Xử lý va chạm giữa bóng và vật thể chung (tường, gạch,...)
    public static void position(Ball ball, GameObject object) {
        if (!Collision.check(ball, object)) return;

        double radius = ball.getRadius();
        double bx = ball.getX();
        double by = ball.getY();
        double ox = object.getX();
        double oy = object.getY();
        double ow = object.getWidth();
        double oh = object.getHeight();

        // Tính khoảng cách từ tâm bóng đến 4 cạnh
        double distLeft   = Math.abs((bx + radius) - ox);
        double distRight  = Math.abs((ox + ow) - (bx - radius));
        double distTop    = Math.abs((by + radius) - oy);
        double distBottom = Math.abs((oy + oh) - (by - radius));

        // Tìm khoảng cách nhỏ nhất (cạnh nào bị chạm trước)
        double minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        double epsilon = 0.01;

        if (minDist == distLeft) {
            // Chạm cạnh trái
            ball.setX(ox - radius - epsilon);
            ball.setDx(-Math.abs(ball.getDx())); // Đảo X sang trái
            } 
            else if (minDist == distRight) {
                    // Chạm cạnh phải
                    ball.setX(ox + ow + radius + epsilon);
                    ball.setDx(Math.abs(ball.getDx())); // Đảo X sang phải
                } 
                else if (minDist == distTop) {
                        // Chạm cạnh trên
                        ball.setY(oy - radius - epsilon);
                        ball.setDy(-Math.abs(ball.getDy())); // Bay lên
                    } 
                    else if (minDist == distBottom) {
                            // Chạm cạnh dưới
                            ball.setY(oy + oh + radius + epsilon);
                            ball.setDy(Math.abs(ball.getDy())); // Bay xuống
                        }
        // Chuẩn hóa lại vận tốc (đảm bảo tốc độ không đổi)
        double speed = ball.getSpeed();
        double v = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
        if (v != 0) {
            ball.setDx(ball.getDx() / v * speed);
            ball.setDy(ball.getDy() / v * speed);
        }
    }
    // Xử lý va chạm bóng với toàn bộ mảng gạch
    // Trả về chỉ số gạch bị va chạm, hoặc -1 nếu không có
    public static int position(Ball ball, Bricks[] bricks) {
        int i = 0;
        for (Bricks brick : bricks) {
            if (Collision.check(ball, brick) && !brick.isBreak()) {
                // Xử lý va chạm vật lý với gạch
                position(ball, brick);

                // Gây sát thương cho gạch theo sức mạnh của bóng
                brick.takeHit((int) ball.getPower());

                // Phát âm thanh phù hợp
                if (brick.isBreak()) {
                    brickBreakSound.play(VolumeManager.getEffectVolume()); // Gạch vỡ hoàn toàn
                } else {
                    Collision.ballBrickSound.play(VolumeManager.getEffectVolume()); // Va chạm nhẹ
                }
                return i; // Trả về chỉ số gạch bị phá
            }
            i++;
        }
        return -1; // Không va chạm gạch nào
    }
}