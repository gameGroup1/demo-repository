/* Lớp cập nhật vị trí của các đối tượng trong trò chơi */
public class update {
    /* Cập nhật vị trí của bóng */
    public void updateBallPosition(Ball ball) {
        if (ball != null) {
            ball.x += ball.dx;
            ball.y += ball.dy;
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào tường */
    public void updateBW(Ball ball, Wall wall, checkCollision collisionChecker) {
        if (collisionChecker.checkBallWall(ball, wall)) {
            if (ball.x - ball.radius <= wall.x || ball.x + ball.radius >= wall.x + wall.width) {
                ball.dx = -ball.dx; 
            }
            if (ball.y - ball.radius <= wall.y || ball.y + ball.radius >= wall.y + wall.height) {
                ball.dy = -ball.dy; 
            }
        }
    }
    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public void updateBP(Ball ball, Paddle paddle, checkCollision collisionChecker) {
        if (collisionChecker.checkBallPaddle(ball, paddle)) {
            ball.dy = -ball.dy; 
            int hitPos = (ball.x - paddle.x) - (paddle.width / 2);
            ball.dx = hitPos / (paddle.width / 2) * ball.speed; 
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

    public void updatePaddlePosition(Paddle paddle, int newX, Wall wall, checkCollision collisionChecker) {
        paddle.x = newX;
        collisionChecker.checkPaddleWall(paddle, wall);
    }
}
