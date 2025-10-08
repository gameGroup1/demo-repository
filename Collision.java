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
}