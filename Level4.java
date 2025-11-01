import java.util.Random;

public class Level4 extends GameLevel {

    public Level4(int wallThickness, int speedC) {
        super(7, 11, 80, 25, 5, wallThickness, speedC); // 7 hàng, 11 cột
        generateMap();
    }

    @Override
    public void generateMap() {
        int[][] crownPattern = {
                {1,0,0,1,0,1,0,1,0,0,1},
                {1,1,0,1,1,1,1,1,0,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,0,0,1,1,1,0,0,1,1},
                {1,1,0,0,1,1,1,0,0,1,1}
        };

        int rows = crownPattern.length;
        int cols = crownPattern[0].length;

        bricks = new Bricks[rows * cols];
        capsules = new Capsule[rows * cols];
        Random random = new Random();

        int[] hardnessArray = {2, 3, 4};
        double offsetX = wallThickness + 50;  // căn giữa
        double offsetY = wallThickness + 100;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;

                if (crownPattern[row][col] == 0) {
                    bricks[index] = null;
                    capsules[index] = null;
                    continue;
                }

                double brickX = offsetX + col * (brickWidth + spacing);
                double brickY = offsetY + row * (brickHeight + spacing);

                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, hardness);

                // 25% cơ hội sinh capsule
                if (random.nextDouble() < 0.25) {
                    capsules[index] = EffectManager.getCapsule(
                            brickX, brickY, brickWidth, brickHeight, speedC
                    );
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                }
            }
        }
    }
}
