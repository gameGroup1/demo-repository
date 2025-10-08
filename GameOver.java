public class GameOver {
    public boolean isGameOver(Ball ball, int heightW) {
        return ball.getY() + ball.getRadius() >= heightW;
    }
}
