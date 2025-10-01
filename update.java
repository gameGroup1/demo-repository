/* Lớp cập nhật vị trí của các đối tượng trong trò chơi */
public class update {
    /* Cập nhật vị trí của bóng */
    public void updateBallPosition(Ball ball) {
        if (ball != null) {
            ball.setX(ball.getX() + ball.getDx());
            ball.setY(ball.getY() + ball.getDy());
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào tường */
    public void updateBW(Ball ball, Wall wall, checkCollision collisionChecker) {
        if (collisionChecker.checkBallWall(ball, wall)) {
            if (ball.getX() - ball.getRadius() <= wall.getX() || ball.getX() + ball.getRadius() >= wall.getX() + wall.getWidth()) {
                ball.setDx(-ball.getDx()); 
            }
            if (ball.getY() - ball.getRadius() <= wall.getY() || ball.getY() + ball.getRadius() >= wall.getY() + wall.getHeight()) {
                ball.setDy(-ball.getDy()); 
            }
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public void updateBP(Ball ball, Paddle paddle, checkCollision collisionChecker) {
        if (collisionChecker.checkBallPaddle(ball, paddle)) {
            ball.setDy(-ball.getDy());
            double ColPosition = ((ball.getX() - paddle.getX()) - (paddle.getWidth() / 2.0)) / (paddle.getWidth() / 2.0);

            if (ColPosition < -1) ColPosition = -1;
            if (ColPosition > 1) ColPosition = 1;
            // Góc lệch tối đa 60 độ (PI/3)
            double maxAngle = Math.PI / 3;
            double angle = ColPosition * maxAngle;
            double speed = ball.getSpeed();

            ball.setDx(speed * Math.sin(angle));
            ball.setDy(-speed * Math.cos(angle));
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào gạch */
    public void updateBB(Ball ball, Bricks[] bricks, checkCollision collisionChecker) {
        for (Bricks brick : bricks) {
            if (!brick.isBreak && collisionChecker.checkBallBricks(ball, brick)) {
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
                break;
            }
        }
    }
    /* Cập nhật vị trí của tấm ván */
    public void updatePaddlePosition(Paddle paddle, int newX, Wall wall, checkCollision collisionChecker) {
        paddle.setX(newX);
        collisionChecker.checkPaddleWall(paddle, wall);
    }
}
