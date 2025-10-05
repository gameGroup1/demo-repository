/* main */
public class mainGame {
    private final int widthW = 400;
    private final int heightW = 600;
    private final int widthP = 100;
    private final int heightP = 20;
    private final int radiusB = 15;
    private final int speedB = 2;

    Ball ball = new Ball((widthW - widthP)/2 + widthP/2, heightW - radiusB - heightP, radiusB, speedB);
    Paddle paddle = new Paddle((widthW - widthP)/2, heightW - heightP, widthP, heightP);
    Wall wall = new Wall(0, 0, widthW, heightW);
    Bricks[] bricks;
    public static void main(String[]args){
        GameMenu.Start();
    }
}
