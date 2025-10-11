import java.awt.Rectangle;

public class Update {
    /* Cập nhật vị trí của bóng */
    public static void position(Ball ball) {
        if (ball == null) return;
        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());
    }

    public static void position(Capsule capsule) {
        if (capsule == null) return;
        capsule.setY(capsule.getY() + capsule.getSpeed());
    }

    public static void position(Ball ball, Paddle paddle) {
        if (!Collision.check(ball, paddle)) return;

        ball.setDy(-ball.getDy());
        double colPosition = ((ball.getX() - paddle.getX()) - (paddle.getWidth() / 2.0)) / (paddle.getWidth() / 2.0);

        if (colPosition < -1) colPosition = -1;
        if (colPosition > 1) colPosition = 1;
        // Góc lệch tối đa 60 độ (PI/3)
        double maxAngle = Math.PI / 3;
        double angle = colPosition * maxAngle;
        double speed = ball.getSpeed();

        ball.setDx(speed * Math.sin(angle));
        ball.setDy(-speed * Math.cos(angle));
    }

    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public static void position(Ball ball, GameObject object) {
        if (Collision.check(ball, object)) {// Sử dụng Collision để kiểm tra
            boolean hitVertical = false, hitHorizontal = false;

            if (ball.getY() < object.getY() || ball.getY() > object.getY() + object.getHeight()) {
                hitVertical = true;
            }

            if (ball.getX() < object.getX() || ball.getX() > object.getX() + object.getWidth()) {
                hitHorizontal = true;
            }

            if (hitVertical && hitHorizontal) {
                ball.setDx(-ball.getDx());
                ball.setDy(-ball.getDy());
            } else if (hitVertical) {
                ball.setDy(-ball.getDy());
            } else if (hitHorizontal) {
                ball.setDx(-ball.getDx());
            } else {
                ball.setDy(-ball.getDy());
            }

            double vectorSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
            if (vectorSpeed != 0) {
                ball.setDx(ball.getDx() / vectorSpeed * ball.getSpeed());
                ball.setDy(ball.getDy() / vectorSpeed * ball.getSpeed());
            }
        }
    }

    /* Cập nhật vị trí của quả bóng sau khi va vào gạch */
    public static Bricks position(Ball ball, Bricks[] bricks) {
        for (Bricks brick : bricks) {
            if (Collision.check(ball, brick) && !brick.isBreak()) {
                position(ball, brick);
                brick.takeHit((int) ball.getPower());
                return brick;
            }
        }
        return null;
    }
}