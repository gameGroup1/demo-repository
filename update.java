public class update {
    public void updateBallPosition(Ball ball) {
        if (ball != null) {
            ball.x += ball.dx;
            ball.y += ball.dy;
        }
    }

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

    public void updateBP(Ball ball, Paddle paddle, checkCollision collisionChecker) {
        if (collisionChecker.checkBallPaddle(ball, paddle)) {
            ball.dy = -ball.dy; 
            int hitPos = (ball.x - paddle.x) - (paddle.width / 2);
            ball.dx = hitPos / (paddle.width / 2) * ball.speed; 
        }
    }

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
                break;
            }
        }
    }

    public void updatePaddlePosition(Paddle paddle, int newX, Wall wall, checkCollision collisionChecker) {
        paddle.x = newX;
        collisionChecker.checkPaddleWall(paddle, wall);
    }
}
