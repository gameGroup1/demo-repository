import java.util.Random;

public class Level7 extends GameLevel {

    public Level7(int wallThickness, int speedC) {
        super(9, 13, 70, 25, 5, wallThickness, speedC);
        generateMap();
    }

    @Override
    public void generateMap() {
        int[][] smileyPattern = {
                {0,0,0,1,1,1,1,1,1,0,0,0,0},
                {0,0,1,1,1,1,1,1,1,1,0,0,0},
                {0,1,1,1,1,0,0,1,1,1,1,0,0},
                {1,1,1,1,0,0,0,0,1,1,1,1,0},
                {1,1,1,1,0,0,0,0,1,1,1,1,0},
                {0,1,1,1,1,0,0,1,1,1,1,0,0},
                {0,0,1,1,1,1,1,1,1,0,0,0,0},
                {0,0,0,1,1,1,1,1,0,0,0,0,0},
                {0,0,0,0,1,1,1,0,0,0,0,0,0}
        };

        int rows = smileyPattern.length;
        int cols = smileyPattern[0].length;

        bricks = new Bricks[rows * cols];
        capsules = new Capsule[rows * cols];
        Random random = new Random();
        int[] hardnessArray = {1, 2, 3};

        // Căn giữa màn hình
        double offsetX = wallThickness + 70;  // căn giữa ngang
        double offsetY = wallThickness + 90;  // căn giữa dọc

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;

                if (smileyPattern[row][col] == 0) {
                    bricks[index] = null;
                    capsules[index] = null;
                    continue;
                }

                double brickX = offsetX + col * (brickWidth + spacing);
                double brickY = offsetY + row * (brickHeight + spacing);

                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, hardness);

                // 20% cơ hội sinh capsule
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
