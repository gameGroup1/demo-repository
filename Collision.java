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

    public  static boolean check(Paddle paddle, Capsule capsule) {
        if (paddle == null || capsule == null) {
            return false;
        }

        double right1 = paddle.getX() + paddle.getWidth();
        double bottom1 = paddle.getY() + paddle.getHeight();
        double right2 = capsule.getX() + capsule.getWidth();
        double bottom2 = capsule.getY() + capsule.getHeight();

        if (right1 < capsule.getX()) return false;
        if (paddle.getX() > right2) return false;
        if (bottom1 < capsule.getY()) return false;
        if (paddle.getY() > bottom2) return false;
        capsule.playSound();
        return true;
    }
}