public class checkCollision {
    public boolean checkBallPaddle(Ball ball, Paddle paddle) {
        // Tìm điểm gần nhất trên paddle đến tâm bóng
        double closestX = clamp(ball.getX(), paddle.getX(), paddle.getX() + paddle.getWidth());
        double closestY = clamp(ball.getY(), paddle.getY(), paddle.getY() + paddle.getHeight());
        
        // Tính khoảng cách từ điểm gần nhất đến tâm bóng
        double distanceX = ball.getX() - closestX;
        double distanceY = ball.getY() - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;
        
        // Kiểm tra va chạm
        return distanceSquared < (ball.getRadius() * ball.getRadius());
    }
    
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public boolean checkBallBricks(Ball ball, Bricks brick) {
        if (brick.isBreak) return false;
        
        // Tìm điểm gần nhất trên brick đến tâm bóng
        double closestX = clamp(ball.getX(), brick.getX(), brick.getX() + brick.getWidth());
        double closestY = clamp(ball.getY(), brick.getY(), brick.getY() + brick.getHeight());
        
        // Tính khoảng cách
        double distanceX = ball.getX() - closestX;
        double distanceY = ball.getY() - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;
        
        return distanceSquared < (ball.getRadius() * ball.getRadius());
    }
    
    public boolean checkBallWall(Ball ball, Wall wall) {
        double nextX = ball.getX() + ball.getDx();
        double nextY = ball.getY() + ball.getDy();
        double radius = ball.getRadius();
        
        // Va chạm trái/phải
        if (nextX - radius < wall.getX() || nextX + radius > wall.getX() + wall.getWidth()) {
            return true;
        }
        
        // Va chạm trên
        if (nextY - radius < wall.getY()) {
            return true;
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