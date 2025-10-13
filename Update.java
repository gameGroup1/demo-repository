import javafx.scene.media.AudioClip;

public class Update {
    public static final AudioClip loseLifeSound;
    public static final AudioClip brickBreakSound;

    static {
        loseLifeSound = new AudioClip(Path.getFileURL(Path.loseLifeSound));
        SoundManager.registerAudioClip(loseLifeSound);
        brickBreakSound = new AudioClip(Path.getFileURL(Path.brickBreakSound));
        SoundManager.registerAudioClip(brickBreakSound);
    }

    public static void position(Capsule capsule) {
        if (capsule == null) return;
        capsule.setY(capsule.getY() + capsule.getSpeed());
    }

    public static void position(Ball ball, Paddle paddle) {
        if (!Collision.check(ball, paddle)) return;

        ball.setDy(-ball.getDy());
        double colPosition = ((ball.getX() - paddle.getX()) - (paddle.getWidth() / 2.0)) / (paddle.getWidth() / 2.0);

        if (colPosition < -1) colPosition = -1;
        if (colPosition > 1) colPosition = 1;
        double maxAngle = Math.PI / 3;
        double angle = colPosition * maxAngle;
        double speed = ball.getSpeed();

        ball.setDx(speed * Math.sin(angle));
        ball.setDy(-speed * Math.cos(angle));
    }

    public static void position(Ball ball, GameObject object) {
        if (!Collision.check(ball, object)) {
            return;
        }

        boolean hitVertical = false, hitHorizontal = false;

        if (ball.getY() < object.getY() || ball.getY() > object.getY() + object.getHeight()) {
            hitVertical = true;
        }

        if (ball.getX() < object.getX() || ball.getX() > object.getX() + object.getWidth()) {
            hitHorizontal = true;
        }

        double epsilon = 0.01;
        double radius = ball.getRadius();

        if (hitVertical) {
            if (ball.getY() < object.getY()) {
                ball.setY(object.getY() - radius - epsilon);
            } else {
                ball.setY(object.getY() + object.getHeight() + radius + epsilon);
            }
        }

        if (hitHorizontal) {
            if (ball.getX() < object.getX()) {
                ball.setX(object.getX() - radius - epsilon);
            } else {
                ball.setX(object.getX() + object.getWidth() + radius + epsilon);
            }
        }

        if (hitVertical && hitHorizontal) {
            ball.setDx(-ball.getDx());
            ball.setDy(-ball.getDy());
        } else if (hitVertical) {
            ball.setDy(-ball.getDy());
        } else if (hitHorizontal) {
            ball.setDx(-ball.getDx());
        } else {
            ball.setDy(-ball.getDy());
        }

        double vectorSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
        if (vectorSpeed != 0) {
            ball.setDx(ball.getDx() / vectorSpeed * ball.getSpeed());
            ball.setDy(ball.getDy() / vectorSpeed * ball.getSpeed());
        }
    }

    public static int position(Ball ball, Bricks[] bricks) {
        int i = 0;
        for (Bricks brick : bricks) {
            if (Collision.check(ball, brick) && !brick.isBreak()) {
                position(ball, brick);
                brick.takeHit((int) ball.getPower());
                if (brick.isBreak()) brickBreakSound.play(SoundManager.getEffectVolume());
                else Collision.ballBrickSound.play(SoundManager.getEffectVolume());
                return i;
            }
            i++;
        }
        return -1;
    }
}