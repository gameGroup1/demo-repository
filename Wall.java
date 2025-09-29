public class Wall {
    public int x, y, width, height;

    public Wall(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean checkCollision(Ball ball) {
        if (ball != null) {
            int ballX = ball.x;
            int ballY = ball.y;
            int ballRadius = ball.radius;

            if (ballX + ballRadius > x && ballX - ballRadius < x + width &&
                ballY + ballRadius > y && ballY - ballRadius < y + height) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollision(Paddle paddle) {
        if (paddle != null) {
            int paddleX = paddle.x;
            int paddleY = paddle.y;
            int paddleWidth = paddle.width;
            int paddleHeight = paddle.height;

            if (paddleX + paddleWidth > x && paddleX < x + width &&
                paddleY + paddleHeight > y && paddleY < y + height) {
                return true;
            }
        }
        return false;
    }
}