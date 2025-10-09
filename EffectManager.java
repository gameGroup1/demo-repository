import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class EffectManager {
    private static final List<Capsule> capsules = new ArrayList<>();// Static list để quản lý multi-balls tạm thời

    public static final Capsule inc10PointCapsule = new Capsule(Path.inc10PointCapsule, Path.getPointSound);
    public static final Capsule dec10PointCapsule = new Capsule(Path.dec10PointCapsule, Path.losePointSound);
    public static final Capsule inc50PointCapsule = new Capsule(Path.inc50PointCapsule, Path.getPointSound);
    public static final Capsule dec50PointCapsule = new Capsule(Path.dec50PointCapsule, Path.losePointSound);
    public static final Capsule inc100PointCapsule = new Capsule(Path.inc100PointCapsule, Path.getPointSound);
    public static final Capsule dec100PointCapsule = new Capsule(Path.dec100PointCapsule, Path.losePointSound);
    public static final Capsule fastBallCapsule = new Capsule(Path.fastBallCapsule, Path.fastSound);
    public static final Capsule slowBallCapsule = new Capsule(Path.slowBallCapsule, Path.slowSound);
    public static final Capsule fireBallCapsule = new Capsule(Path.fireBallCapsule, Path.fireSound);
    public static final Capsule toxicBallCapsule = new Capsule(Path.toxicBallCapsule, Path.toxicSound);
    public static final Capsule powerBallCapsule = new Capsule(Path.powerBallCapsule, Path.powerUpSound);
    public static final Capsule expandPaddleCapsule = new Capsule(Path.expandPaddleCapsule, Path.transformSound);
    public static final Capsule shrinkPaddleCapsule = new Capsule(Path.shrinkPaddleCapsule, Path.transformSound);

    static {
        capsules.add(inc10PointCapsule);
        capsules.add(dec10PointCapsule);
        capsules.add(inc50PointCapsule);
        capsules.add(dec50PointCapsule);
        capsules.add(inc100PointCapsule);
        capsules.add(dec100PointCapsule);
        capsules.add(fastBallCapsule);
        capsules.add(slowBallCapsule);
        capsules.add(fireBallCapsule);
        capsules.add(toxicBallCapsule);
        capsules.add(powerBallCapsule);
        capsules.add(expandPaddleCapsule);
        capsules.add(shrinkPaddleCapsule);
    }

    public static Capsule getCapsule(double x, double y, int width, int height, double speed) {
        Random random = new Random();
        int randomIndex = random.nextInt(capsules.size());
        Capsule capsule = capsules.get(randomIndex);
        capsule.init(x, y, width, height, speed);
        return capsule;
    }

    public static void updatePower(Ball ball, double factor) {
        if (ball == null) {
            return;  // Xử lý edge case: ball null
        }

        int originalPower = ball.getPower();
        ball.setPower((int) (originalPower * factor));  // Tăng sức mạnh theo hệ số factor

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    ball.setPower(originalPower);
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }

    public static void updateSpeed(Ball ball, double factor) {
        if (ball == null) {
            return;  // Xử lý edge case: ball null
        }

        double originalSpeed = ball.getSpeed();
        ball.setSpeed(originalSpeed * factor);
        double angle = Math.atan2(ball.getDy(), ball.getDx());
        ball.setDx(ball.getSpeed() * Math.cos(angle));
        ball.setDy(ball.getSpeed() * Math.sin(angle));

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    ball.setSpeed(originalSpeed);
                    double angleRestore = Math.atan2(ball.getDy(), ball.getDx());
                    ball.setDx(ball.getSpeed() * Math.cos(angleRestore));
                    ball.setDy(ball.getSpeed() * Math.sin(angleRestore));
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }

    public static void changeWidth(Paddle paddle, double factor) {
        if (paddle == null) {
            return;  // Xử lý edge case: paddle null
        }

        int originalWidth = paddle.getWidth();
        paddle.setWidth((int) (originalWidth * factor));

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    paddle.setWidth(originalWidth);
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }
}