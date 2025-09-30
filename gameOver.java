public class gameOver {
    public boolean isGameOver(Ball ball, int heightW) {
        return ball.y + ball.radius >= heightW;
    }
}
