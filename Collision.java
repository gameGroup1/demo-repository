public class Collision {

    public static boolean check(Ball ball, GameObject object) {
        if (ball == null || object == null) {
            return false;
        }

        double closestX = Math.max(object.getX(), Math.min(ball.getX(), object.getX() + object.getWidth()));
        double closestY = Math.max(object.getY(), Math.min(ball.getY(), object.getY() + object.getHeight()));

        double distanceX = ball.getX() - closestX;
        double distanceY = ball.getY() - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        if (distanceSquared <= (ball.getRadius() * ball.getRadius())){
            Material.playSound(ball.getMaterial(), object.getMaterial());
            return true;
        }
        return false;
    }

    public static boolean check(Paddle paddle, Wall wall) {
        if (paddle == null || wall == null) {
            return false; // Xử lý lỗi
        }

        return paddle.getX() < wall.getX() + wall.getWidth() &&
                paddle.getX() + paddle.getWidth() > wall.getX() &&
                paddle.getY() < wall.getY() + wall.getHeight() &&
                paddle.getY() + paddle.getHeight() > wall.getY();
    }
}