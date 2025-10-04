public class GameObjects {
    private final int widthW;
    private final int heightW;
    private final int widthP;
    private final int heightP;
    private final int radiusB;
    private final int speedB;

    // Constructor nhận các hằng số từ MainGame
    public GameObjects(int widthW, int heightW, int widthP, int heightP, int radiusB, int speedB) {
        this.widthW = widthW;
        this.heightW = heightW;
        this.widthP = widthP;
        this.heightP = heightP;
        this.radiusB = radiusB;
        this.speedB = speedB;
    }

    // Phương thức khởi tạo và trả về tất cả đối tượng game
    public GameObjectsHolder createAll() {
        Ball ball = new Ball((widthW - widthP)/2 + widthP/2, heightW - radiusB - heightP, radiusB, speedB);
        ball.setDx(speedB);  // Set hướng ban đầu (phải và lên)
        ball.setDy(-speedB);

        Paddle paddle = new Paddle((widthW - widthP)/2, heightW - heightP, widthP, heightP);
        Wall wall = new Wall(0, 0, widthW, heightW);
        Bricks[] bricks = createBricks();  // Gọi hàm tạo bricks (copy từ code cũ)

        return new GameObjectsHolder(ball, paddle, wall, bricks);
    }

    // Hàm phụ tạo mảng bricks (tương tự code cũ)
    private Bricks[] createBricks() {
        java.util.List<Bricks> brickList = new java.util.ArrayList<>();
        int brickWidth = 50;
        int brickHeight = 20;
        int rows = 5;
        int cols = widthW / brickWidth;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * brickWidth;
                int y = row * brickHeight + 50; // Độ lệch từ trên xuống
                brickList.add(new Bricks(x, y, brickWidth, brickHeight));
            }
        }
        return brickList.toArray(new Bricks[0]);
    }

    // Class nội bộ để "gói" tất cả đối tượng trả về (như một DTO - Data Transfer Object)
    public static class GameObjectsHolder {
        public final Ball ball;
        public final Paddle paddle;
        public final Wall wall;
        public final Bricks[] bricks;

        public GameObjectsHolder(Ball ball, Paddle paddle, Wall wall, Bricks[] bricks) {
            this.ball = ball;
            this.paddle = paddle;
            this.wall = wall;
            this.bricks = bricks;
        }
    }
}