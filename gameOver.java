public class gameOver {
    public boolean isGameOver(Ball ball, int heightW) {
        return ball.getY() + ball.getRadius() >= heightW;
    }
}