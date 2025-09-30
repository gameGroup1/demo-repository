public class checkCollision {
    public boolean checkBallWall(Ball ball, Wall wall) {
        if (ball != null && wall != null) {
            int ballX = ball.x;
            int ballY = ball.y;
            int ballRadius = ball.radius;

            if (ballX + ballRadius > wall.x && ballX - ballRadius < wall.x + wall.width &&
                ballY + ballRadius > wall.y && ballY - ballRadius < wall.y + wall.height) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBallPaddle(Ball ball, Paddle paddle) {
        if (ball != null && paddle != null) {
            int ballX = ball.x;
            int ballY = ball.y;
            int ballRadius = ball.radius;

            if (ballX + ballRadius > paddle.x && ballX - ballRadius < paddle.x + paddle.width &&
                ballY + ballRadius > paddle.y && ballY - ballRadius < paddle.y + paddle.height) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBallBricks(Ball ball, Bricks brick) {
        if (ball != null && brick != null && !brick.isBreak) {
            int ballX = ball.x;
            int ballY = ball.y;
            int ballRadius = ball.radius;

            if (ballX + ballRadius > brick.x && ballX - ballRadius < brick.x + brick.width &&
                ballY + ballRadius > brick.y && ballY - ballRadius < brick.y + brick.height) {
                return true;
            }
        }
        return false;
    }

    public boolean checkPaddleWall(Paddle paddle, Wall wall) {
        if (paddle != null && wall != null) {
            int paddleX = paddle.x;
            
            if (paddleX < wall.x) {
                paddle.x = wall.x;
                return true;
            } else if (paddleX + paddle.width > wall.x + wall.width) {
                paddle.x = wall.x + wall.width - paddle.width;
                return true;
            }
        }
        return false;
    }
}