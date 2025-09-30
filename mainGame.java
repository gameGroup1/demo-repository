public class mainGame {
    private int widthW = 400, heightW = 600;
    private int widthP = 100, heightP = 20;
    private int widthB = widthW/2, radiusB = 15, heightB = 30, speedB = 2;

    Ball ball = new Ball((widthW - widthP)/2 + widthP/2, heightW - radiusB - heightP, radiusB, speedB);
    Paddle paddle = new Paddle((widthW - widthP)/2, heightW - heightP, widthP, heightP);
    Wall wall = new Wall(0, 0, widthW, heightW);
    Bricks[] bricks;
    public static void main(String[]args){
        
    }
}
