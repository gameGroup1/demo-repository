import java.util.Random;

public class Level2 extends GameLevel {

    public Level2(int wallThickness, int speedC) {
        super(6, 11, 80, 25, 5, wallThickness, speedC);
        generateMap();
    }

    @Override
    public void generateMap() {
        int[] hardnessArray = {2, 3, 4};
        Random random = new Random();

        bricks = new Bricks[rowCount * colCount];
        capsules = new Capsule[rowCount * colCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // tạo khoảng trống giữa map
                if (row >= 2 && row <= 3 && col >= 4 && col <= 7) {
                    bricks[row * colCount + col] = null;
                    capsules[row * colCount + col] = null;
                    continue;
                }

                double brickX = col * (brickWidth + spacing) + wallThickness + 40;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;

                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, hardness);

                if (random.nextDouble() < 0.25) {
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                }
            }
        }
    }
}
