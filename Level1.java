import java.util.Random;

public class Level1 extends GameLevel {

    public Level1(int wallThickness, int speedC) {
        super(5, 10, 90, 30, 5, wallThickness, speedC);
        generateMap();
    }

    @Override
    public void generateMap() {
        int[] hardnessArray = {1, 2, 3, 4};
        Random random = new Random();

        bricks = new Bricks[rowCount * colCount];
        capsules = new Capsule[rowCount * colCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                double brickX = col * (brickWidth + spacing) + wallThickness + 30;
                double brickY = row * (brickHeight + spacing) + wallThickness + 100;
                int index = row * colCount + col;

                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];
                bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, hardness);

                double chance = random.nextDouble();
                if (chance < 0.3) {
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                } else if (chance < 0.5) {
                    capsules[index] = new Capsule(Path.explosionCapsule, Path.explosionSound);
                    capsules[index].init(brickX, brickY, brickWidth, brickHeight, speedC, "explosion");
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                } else {
                    capsules[index] = null;
                }
            }
        }
    }
}
