/* Lớp cập nhật vị trí của các đối tượng trong trò chơi */
public class Update {
    /* Cập nhật vị trí của bóng */
    public static void position(Ball ball) {
        if (ball != null) {
            ball.setX(ball.getX() + ball.getDx());
            ball.setY(ball.getY() + ball.getDy());
        }
    }

    /* Cập nhật vị trí của tấm ván (gọi sau move của Paddle) */
    public static void position(Paddle paddle, Wall wall) {
        if (Collision.check(paddle, wall)) { // Kiểm tra va chạm để điều chỉnh nếu cần
            // Giới hạn vị trí paddle để không vượt ra ngoài biên wall
            if (paddle.getX() < wall.getX()) {
                paddle.setX(wall.getX());
            }
            if (paddle.getX() + paddle.getWidth() > wall.getX() + wall.getWidth()) {
                paddle.setX(wall.getX() + wall.getWidth() - paddle.getWidth());
            }
        }
    }

    /* Cập nhật vị trí của quả bóng sau khi va vào tường */
    public static void position(Ball ball, Wall wall) {
        if (Collision.check(ball, wall)) { // Sử dụng Collision để kiểm tra
            if (ball.getX() - ball.getRadius() <= wall.getX() || ball.getX() + ball.getRadius() >= wall.getX() + wall.getWidth()) {
                ball.setDx(-ball.getDx());
            }
            if (ball.getY() - ball.getRadius() <= wall.getY() || ball.getY() + ball.getRadius() >= wall.getY() + wall.getHeight()) {
                ball.setDy(-ball.getDy());
            }
        }
    }

    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public static void position(Ball ball, Paddle paddle) {
        if (Collision.check(ball, paddle)) { // Sử dụng Collision để kiểm tra
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
    }

    /* Cập nhật vị trí của quả bóng sau khi va vào gạch */
    public static void position(Ball ball, Bricks[] bricks) {
        for (Bricks brick : bricks) {
            if (!brick.isBreak && Collision.check(ball, brick)) { // Sử dụng Collision để kiểm tra
                brick.isBreak = true;
                boolean hitVertical = false, hitHorizontal = false;

                if (ball.getY() < brick.getY() || ball.getY() > brick.getY() + brick.getHeight()) {
                    hitVertical = true;
                }

                if (ball.getX() < brick.getX() || ball.getX() > brick.getX() + brick.getWidth()) {
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
                break; // Chỉ xử lý một va chạm mỗi lần
            }
        }
    }
}
