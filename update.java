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
            if (ball.getX() - ball.getRadius() <= wall.x || ball.getX() + ball.getRadius() >= wall.x + wall.width) {
                ball.setDx(-ball.getDx()); 
            }
            if (ball.getY() - ball.getRadius() <= wall.y || ball.getY() + ball.getRadius() >= wall.y + wall.height) {
                ball.setDy(-ball.getDy()); 
            }
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public void updateBP(Ball ball, Paddle paddle, checkCollision collisionChecker) {
        if (collisionChecker.checkBallPaddle(ball, paddle)) {
            ball.dy = -ball.dy;
            double ColPosition = ((ball.x - paddle.x) - (paddle.width / 2.0)) / (paddle.width / 2.0);

            if (ColPosition < -1) ColPosition = -1;
            if (ColPosition > 1) ColPosition = 1;
            // Góc lệch tối đa 60 độ (PI/3)
            double maxAngle = Math.PI / 3;
            double angle = ColPosition * maxAngle;
            double speed = ball.speed;

            ball.dx = speed * Math.sin(angle);
            ball.dy = -speed * Math.cos(angle);
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào gạch */
    public void updateBB(Ball ball, Bricks[] bricks, checkCollision collisionChecker) {
        for (Bricks brick : bricks) {
            if (!brick.isBreak && collisionChecker.checkBallBricks(ball, brick)) {
                brick.isBreak = true;
                boolean hitVertical = false, hitHorizontal = false;

                if (ball.y < brick.y || ball.y > brick.y + brick.height) {
                    hitVertical = true;
                }

                if (ball.x < brick.x || ball.x > brick.x + brick.width) {
                    hitHorizontal = true;
                }

                if (hitVertical && hitHorizontal) {
                    ball.dx = -ball.dx;
                    ball.dy = -ball.dy;
                } else if (hitVertical) {
                    ball.dy = -ball.dy;
                } else if (hitHorizontal) {
                    ball.dx = -ball.dx;
                } else {
                    ball.dy = -ball.dy;
                }

                double vectorSpeed = Math.sqrt(ball.dx * ball.dx + ball.dy * ball.dy);
                if (vectorSpeed != 0) {
                    ball.dx = ball.dx / vectorSpeed * ball.speed;
                    ball.dy = ball.dy / vectorSpeed * ball.speed;
                }
                break;
            }
        }
    }
    /* Cập nhật vị trí của tấm ván */
    public void updatePaddlePosition(Paddle paddle, int newX, Wall wall, checkCollision collisionChecker) {
        paddle.x = newX;
        collisionChecker.checkPaddleWall(paddle, wall);
    }
}
