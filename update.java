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
            // Va chạm trái/phải
            if (ball.getX() - ball.getRadius() <= wall.getX() || 
                ball.getX() + ball.getRadius() >= wall.getX() + wall.getWidth()) {
                ball.setDx(-ball.getDx()); 
            }
            // Va chạm trên/dưới
            if (ball.getY() - ball.getRadius() <= wall.getY() || 
                ball.getY() + ball.getRadius() >= wall.getY() + wall.getHeight()) {
                ball.setDy(-ball.getDy()); 
            }
        }
    }
    
    /* Cập nhật vị trí của quả bóng sau khi va vào tấm ván */
    public void updateBP(Ball ball, Paddle paddle, checkCollision collisionChecker) {
        if (collisionChecker.checkBallPaddle(ball, paddle)) {
            // Tính toán vị trí va chạm tương đối trên paddle (-1 đến 1)
            double collisionPoint = ((ball.getX() - paddle.getX()) - (paddle.getWidth() / 2.0)) / (paddle.getWidth() / 2.0);
            
            // Giới hạn giá trị trong khoảng [-1, 1]
            collisionPoint = Math.max(-1.0, Math.min(1.0, collisionPoint));
            
            // Góc lệch tối đa (75 độ = 5π/12 radian)
            double maxAngle = 5 * Math.PI / 12;
            double angle = collisionPoint * maxAngle;
            
            // Giữ nguyên tốc độ hiện tại của bóng
            double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
            
            // Tính toán hướng mới dựa trên góc
            double newDx = speed * Math.sin(angle);
            double newDy = -speed * Math.cos(angle); // Luôn đi lên
            
            // Đảm bảo bóng không bị kẹt trong paddle
            double ballBottom = ball.getY() + ball.getRadius();
            double paddleTop = paddle.getY();
            
            if (ballBottom > paddleTop) {
                ball.setY(paddleTop - ball.getRadius() - 1);
            }
            
            // Áp dụng vận tốc mới
            ball.setDx(newDx);
            ball.setDy(newDy);
            
            // Tăng tốc độ nhẹ sau mỗi lần va chạm
            double speedIncrease = 1.02;
            ball.setDx(ball.getDx() * speedIncrease);
            ball.setDy(ball.getDy() * speedIncrease);
        }
    }
    
    /* Cập nhật vị trí của quả bóng sau khi va vào gạch - FIXED */
    public void updateBB(Ball ball, Bricks[] bricks, checkCollision collisionChecker) {
        for (Bricks brick : bricks) {
            if (!brick.isBreak && collisionChecker.checkBallBricks(ball, brick)) {
                brick.isBreak = true;
                
                // Tính khoảng cách từ tâm bóng đến các cạnh của gạch
                double ballCenterX = ball.getX();
                double ballCenterY = ball.getY();
                double radius = ball.getRadius();
                
                // Khoảng cách đến mỗi cạnh
                double distanceTop = Math.abs(ballCenterY - brick.getY());
                double distanceBottom = Math.abs(ballCenterY - (brick.getY() + brick.getHeight()));
                double distanceLeft = Math.abs(ballCenterX - brick.getX());
                double distanceRight = Math.abs(ballCenterX - (brick.getX() + brick.getWidth()));
                
                // Tìm cạnh gần nhất
                double minDistance = Math.min(
                    Math.min(distanceTop, distanceBottom),
                    Math.min(distanceLeft, distanceRight)
                );
                
                // Đổi hướng dựa trên cạnh va chạm
                if (minDistance == distanceTop || minDistance == distanceBottom) {
                    // Va chạm từ trên hoặc dưới -> đảo chiều Y
                    ball.setDy(-ball.getDy());
                } else {
                    // Va chạm từ trái hoặc phải -> đảo chiều X
                    ball.setDx(-ball.getDx());
                }
                
                // Chuẩn hóa tốc độ
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