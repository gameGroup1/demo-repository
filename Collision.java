/* Lớp kiểm tra các đối tượng trong trò chơi */
public class Collision {
    public static boolean check(Ball ball, Wall wall) {
        if (ball == null || wall == null) {
            return false;
        }

        double ballX = ball.getX();
        double ballY = ball.getY();
        double ballRadius = ball.getRadius();

        return ballX + ballRadius > wall.getX() &&
                ballX - ballRadius < wall.getX() + wall.getWidth() &&
                ballY + ballRadius > wall.getY() &&
                ballY - ballRadius < wall.getY() + wall.getHeight();
    }

    public static boolean check(Ball ball, Bricks brick) {
        if (ball != null && brick != null && !brick.isBreak) {
            double ballX = ball.getX();
            double ballY = ball.getY();
            double ballRadius = ball.getRadius();

            if (ballX + ballRadius > brick.getX() && ballX - ballRadius < brick.getX() + brick.getWidth() &&
                    ballY + ballRadius > brick.getY() && ballY - ballRadius < brick.getY() + brick.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public static boolean check(Ball ball, Paddle paddle) {
        // Lấy thông tin của Ball (Hình tròn)
        double ballX = ball.getX();
        double ballY = ball.getY();
        double ballRadius = ball.getRadius();

        // Lấy thông tin của Paddle (Hình chữ nhật)
        double paddleX = paddle.getX();
        double paddleY = paddle.getY();
        int paddleWidth = paddle.getWidth();
        int paddleHeight = paddle.getHeight();

        // 1. Tìm điểm gần nhất trên hình chữ nhật (Paddle) với tâm hình tròn (Ball)

        // Điểm gần nhất theo trục X (Clamping)
        double closestX = ballX;
        if (ballX < paddleX) {
            closestX = paddleX;
        } else if (ballX > paddleX + paddleWidth) {
            closestX = paddleX + paddleWidth;
        }

        // Điểm gần nhất theo trục Y (Clamping)
        double closestY = ballY;
        if (ballY < paddleY) {
            closestY = paddleY;
        } else if (ballY > paddleY + paddleHeight) {
            closestY = paddleY + paddleHeight;
        }

        // 2. Tính khoảng cách bình phương từ tâm Ball đến điểm gần nhất
        double distanceX = ballX - closestX;
        double distanceY = ballY - closestY;

        // Khoảng cách bình phương (tránh dùng Math.sqrt() để tăng hiệu suất)
        double distanceSq = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSq <= (ballRadius * ballRadius);
    }
}