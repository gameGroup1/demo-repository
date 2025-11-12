package code_for_manager;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

abstract public class EffectManager {
    private static final List<String> effectTypes = new ArrayList<>(List.of(
            "inc10Point", "dec10Point", "inc50Point", "dec50Point", "inc100Point", "dec100Point",
            "fastBall", "slowBall", "fireBall", "powerBall", "expandPaddle", "shrinkPaddle", "healthCapsule"
    ));

    public static Capsule getCapsule(double x, double y, int width, int height, double speed) {
        Random random = new Random();
        String type = effectTypes.get(random.nextInt(effectTypes.size()));

        // Tạo instance Capsule mới cho mỗi gạch
        String imagePath = getImagePathForType(type);
        String soundPath = getSoundPathForType(type);
        Capsule capsule = new Capsule(imagePath, soundPath);
        capsule.init(x, y, width, height, speed, type); // Vị trí và kích thước phù hợp
        capsule.setVisible(false);
        return capsule;
    }

    private static String getImagePathForType(String type) {
        if (type.equals("inc10Point")) {
            return Path.inc10PointCapsule;
        } else if (type.equals("dec10Point")) {
            return Path.dec10PointCapsule;
        } else if (type.equals("inc50Point")) {
            return Path.inc50PointCapsule;
        } else if (type.equals("dec50Point")) {
            return Path.dec50PointCapsule;
        } else if (type.equals("inc100Point")) {
            return Path.inc100PointCapsule;
        } else if (type.equals("dec100Point")) {
            return Path.dec100PointCapsule;
        } else if (type.equals("fastBall")) {
            return Path.fastBallCapsule;
        } else if (type.equals("slowBall")) {
            return Path.slowBallCapsule;
        } else if (type.equals("fireBall")) {
            return Path.fireBallCapsule;
        } else if (type.equals("powerBall")) {
            return Path.powerBallCapsule;
        } else if (type.equals("expandPaddle")) {
            return Path.expandPaddleCapsule;
        } else if (type.equals("shrinkPaddle")) {
            return Path.shrinkPaddleCapsule;
        } else if (type.equals("healthCapsule")) {
            return Path.healthCapsule;
        } else {
            return null; // Or a default path
        }
    }

    private static String getSoundPathForType(String type) {
        if (type.equals("inc10Point") || type.equals("inc50Point") || type.equals("inc100Point")) {
            return Path.getPointSound;
        } else if (type.equals("dec10Point") || type.equals("dec50Point") || type.equals("dec100Point")) {
            return Path.losePointSound;
        } else if (type.equals("fastBall")) {
            return Path.fastSound;
        } else if (type.equals("slowBall")) {
            return Path.slowSound;
        } else if (type.equals("fireBall")) {
            return Path.fireSound;
        } else if (type.equals("powerBall") ) {
            return Path.powerUpSound;
        } else if (type.equals("expandPaddle") || type.equals("shrinkPaddle")) {
            return Path.transformSound;
        } else if (type.equals("healthCapsule")){
            return Path.healingSound;
        } else {
            return null;
        }
    }

    public static void updatePower(Ball ball, double factor, int defaultPower) {
        if (ball == null) {
            return;  // Xử lý edge case: ball null
        }

        ball.setPower((int) (ball.getPower() * factor));  // Tăng sức mạnh theo hệ số factor

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    ball.setPower(defaultPower);
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }

    public static void updateSpeed(Ball ball, double factor, double defaultSpeed) {
        if (ball == null) {
            return;  // Xử lý edge case: ball null
        }

        ball.setSpeed(ball.getSpeed() * factor);
        double angle = Math.atan2(ball.getDy(), ball.getDx());
        ball.setDx(ball.getSpeed() * Math.cos(angle));
        ball.setDy(ball.getSpeed() * Math.sin(angle));

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    ball.setSpeed(defaultSpeed);
                    double angleRestore = Math.atan2(ball.getDy(), ball.getDx());
                    ball.setDx(ball.getSpeed() * Math.cos(angleRestore));
                    ball.setDy(ball.getSpeed() * Math.sin(angleRestore));
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }

    public static void changeWidth(Paddle paddle, double factor, int defaultWidth) {
        if (paddle == null) {
            return;  // Xử lý edge case: paddle null
        }

        paddle.setWidth((int) (paddle.getWidth() * factor));

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    paddle.setWidth(defaultWidth);
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }

    public static void activateFireBall(Ball ball) {
        if (ball == null) {
            return;  // Xử lý edge case: ball null
        }
        int originalPower = ball.getPower();
        ball.setPower(10);  // Giả sử fireball làm bóng phá gạch ngay lập tức bằng cách tăng power cao
        ball.setFireBall(true);

        // Sử dụng Timeline để khôi phục sau 5 giây (5000ms)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                event -> {
                    ball.setPower(originalPower);
                    ball.setFireBall(false);
                }
        ));
        timeline.setCycleCount(1);  // Chạy một lần
        timeline.play();  // Bắt đầu timer
    }
}