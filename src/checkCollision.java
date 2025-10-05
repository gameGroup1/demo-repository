/* Lớp kiểm tra các đối tượng trong trò chơi */
public class checkCollision {
    public boolean checkBallWall(Ball ball, Wall wall) {
        if (ball != null && wall != null) {
            double ballX = ball.getX();
            double ballY = ball.getY();
            double ballRadius = ball.getRadius();

            if (ballX + ballRadius > wall.getX() && ballX - ballRadius < wall.getX() + wall.getWidth() &&
                ballY + ballRadius > wall.getY() && ballY - ballRadius < wall.getY() + wall.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBallPaddle(Ball ball, Paddle paddle) {
        if (ball != null && paddle != null) {
            int ballX = (int) ball.getX();
            int ballY = (int) ball.getY();
            int ballRadius = (int) ball.getRadius();

            if (ballX + ballRadius > paddle.getX() && ballX - ballRadius < paddle.getX() + paddle.getWidth() &&
                ballY + ballRadius > paddle.getY() && ballY - ballRadius < paddle.getY() + paddle.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBallBricks(Ball ball, Bricks brick) {
        if (ball != null && brick != null && !brick.isBreak) {
            int ballX = (int) ball.getX();
            int ballY = (int) ball.getY();
            int ballRadius = (int) ball.getRadius();

            if (ballX + ballRadius > brick.getX() && ballX - ballRadius < brick.getX() + brick.getWidth() &&
                ballY + ballRadius > brick.getY() && ballY - ballRadius < brick.getY() + brick.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkPaddleWall(Paddle paddle, Wall wall) {
        if (paddle != null && wall != null) {
            int paddleX = paddle.getX();

            if (paddleX < wall.getX()) {
                paddle.setX(wall.getX());
                return true;
            } else if (paddleX + paddle.getWidth() > wall.getX() + wall.getWidth()) {
                paddle.setX(wall.getX() + wall.getWidth() - paddle.getWidth());
                return true;
            }
        }
        return false;
    }
}