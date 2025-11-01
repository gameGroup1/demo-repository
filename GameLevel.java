import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GameLevel {
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

    public GameLevel(int rowCount, int colCount, int brickWidth, int brickHeight,
                     int spacing, int wallThickness, int speedC) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.brickWidth = brickWidth;
        this.brickHeight = brickHeight;
        this.spacing = spacing;
        this.wallThickness = wallThickness;
        this.speedC = speedC;
    }

    public abstract void generateMap();

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
