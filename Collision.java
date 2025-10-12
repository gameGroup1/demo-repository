import javafx.scene.media.AudioClip;

public class Collision {
    public static final AudioClip ballWallSound;
    public static final AudioClip ballPaddleSound;
    public static final AudioClip ballBrickSound;

    static {
        ballWallSound = new AudioClip(Path.getFileURL(Path.ballWallSound));
        SoundManager.registerAudioClip(ballWallSound);
        ballPaddleSound = new AudioClip(Path.getFileURL(Path.ballPaddleSound));
        SoundManager.registerAudioClip(ballPaddleSound);
        ballBrickSound = new AudioClip(Path.getFileURL(Path.ballBrickSound));
        SoundManager.registerAudioClip(ballBrickSound);
    }

    public static boolean check(Ball ball, GameObject object) {
        if (ball == null || object == null) {
            return false;
        }

        double closestX = Math.max(object.getX(), Math.min(ball.getX(), object.getX() + object.getWidth()));
        double closestY = Math.max(object.getY(), Math.min(ball.getY(), object.getY() + object.getHeight()));

        double distanceX = ball.getX() - closestX;
        double distanceY = ball.getY() - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        if (distanceSquared <= ball.getRadius() * ball.getRadius()) {
            if (object instanceof Paddle) {
                ballPaddleSound.play(SoundManager.getGlobalVolume());
            } else if (object instanceof Wall) {
                ballWallSound.play(SoundManager.getGlobalVolume());
            }
            return true;
        }
        return false;
    }

    public static boolean check(Paddle paddle, Capsule capsule) {
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
        capsule.playSound(SoundManager.getGlobalVolume());
        return true;
    }
}