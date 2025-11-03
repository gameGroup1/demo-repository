import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;

public class LevelSpecial extends GameLevel {
    private Timeline moveTimeline;
    private double moveSpeed = 1.5; // tốc độ di chuyển
    private double moveRange = 50;  // phạm vi dao động (px)
    private double[] originalX;     // lưu vị trí gốc
    private double direction = 1;   // 1 = sang phải, -1 = sang trái

    public LevelSpecial(int wallThickness, int speedC) {
        super(6, 10, 80, 30, 5, wallThickness, speedC);
        generateMap();
        setupMovingAnimation();
    }

    @Override
    public void generateMap() {
        int total = rowCount * colCount;
        bricks = new Bricks[total];
        capsules = new Capsule[total];
        Random random = new Random();

        int[] hardnessArray = {1, 2, 3};
        double offsetX = wallThickness + 80;
        double offsetY = wallThickness + 60;

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                int index = i * colCount + j;
                int hardness = hardnessArray[random.nextInt(hardnessArray.length)];

                double x = offsetX + j * (brickWidth + spacing);
                double y = offsetY + i * (brickHeight + spacing);

                bricks[index] = new Bricks(x, y, brickWidth, brickHeight, hardness);

                // 20% cơ hội sinh capsule
                if (random.nextDouble() < 0.2) {
                    capsules[index] = EffectManager.getCapsule(x, y, brickWidth, brickHeight, speedC);
                    capsules[index].setVisible(false);
                    capsuleIndex.add(index);
                } else {
                    capsules[index] = null;
                }
            }
        }
    }

    private void setupMovingAnimation() {
        int total = rowCount * colCount;
        originalX = new double[total];

        for (int i = 0; i < total; i++) {
            if (bricks[i] != null) {
                originalX[i] = bricks[i].getX();
            }
        }

        moveTimeline = new Timeline(new KeyFrame(Duration.millis(30), e -> moveBricks()));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();
    }

    private void moveBricks() {
        boolean reverse = false;

        for (int i = 0; i < bricks.length; i++) {
            Bricks b = bricks[i];
            if (b == null) continue;

            double newX = b.getX() + moveSpeed * direction;
            double diff = newX - originalX[i];
            if (Math.abs(diff) >= moveRange) reverse = true;

            b.setPosition(newX, b.getY());
        }

        if (reverse) direction *= -1;
    }
}
