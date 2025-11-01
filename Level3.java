import java.util.Random;

public class Level3 extends GameLevel {

    public Level3(int wallThickness, int speedC) {
        super(7, 9, 80, 25, 5, wallThickness, speedC); // giảm hàng & cột
        generateMap();
    }

    @Override
    public void generateMap() {
        int[][] heartPattern = {
                {0, 1, 1, 0, 0, 1, 1, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0}
        };

        int rows = heartPattern.length;
        int cols = heartPattern[0].length;

        bricks = new Bricks[rows * cols];
        capsules = new Capsule[rows * cols];
        Random random = new Random();

        int[] hardnessArray = {1, 2, 3};
        double offsetX = wallThickness + 130; // căn giữa trái tim
        double offsetY = wallThickness + 100;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;

                if (heartPattern[row][col] == 0) {
                    bricks[index] = null;
                    capsules[index] = null;
                    continue;
                }

                double brickX = offsetX + col * (brickWidth + spacing);
                double brickY = offsetY + row * (brickHeight + spacing);

                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, hardness);

                if (random.nextDouble() < 0.2) {
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