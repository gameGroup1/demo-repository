package code_for_levels;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {
    protected Bricks[] bricks;
    protected Capsule[] capsules;
    protected List<Integer> capsuleIndex = new ArrayList<>();

    protected int rowCount;
    protected int colCount;
    protected int brickWidth;
    protected int brickHeight;
    protected int spacing;
    protected int wallThickness;
    protected int speedC;

    public Level(int rowCount, int colCount, int brickWidth, int brickHeight, int spacing, int wallThickness, int speedC) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.brickWidth = brickWidth;
        this.brickHeight = brickHeight;
        this.spacing = spacing;
        this.wallThickness = wallThickness;
        this.speedC = speedC;
    }

    public void makeBricks(int[][] arr) {
        int rows = arr.length;
        int cols = arr[0].length;

        bricks = new Bricks[rows * cols];
        capsules = new Capsule[rows * cols];
        Random random = new Random();

        int[] hardnessArray = {1, 2, 3, 4};
        double offsetX = wallThickness + 50;  // căn giữa
        double offsetY = wallThickness + 100;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;

                if (arr[row][col] == 0) {
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
                    capsules[index] = EffectManager.getCapsule(brickX, brickY, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                }
                else if (random.nextDouble() < 0.2) {
                    capsules[index] = new Capsule(Path.explosionCapsule, Path.explosionSound);
                    capsules[index].init(brickX, brickY, brickWidth, brickHeight, speedC, "explosion");
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                }
            }
        }
    }

    public Bricks[] getBricks() {
        return bricks;
    }
    public Capsule[] getCapsules() {
        return capsules;
    }
    public List<Integer> getCapsuleIndex() {
        return capsuleIndex;
    }
}
