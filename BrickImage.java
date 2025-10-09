import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

public class BrickImage {
    private static final int x = 0;
    private static final int y = 0;
    private static final int width = 384;
    private static final int height = 128;
    public static final List<Rectangle2D> woodFrame = new ArrayList<>();
    public static final List<Rectangle2D> metalFrame = new ArrayList<>();
    public static final List<Rectangle2D> rockFrame = new ArrayList<>();
    public static final List<Rectangle2D> jewelFrame = new ArrayList<>();
    public static final Image woodSprite = new Image(Path.getFileURL(Path.woodSprite));
    public static final Image metalSprite = new Image(Path.getFileURL(Path.metalSprite));
    public static final Image rockSprite = new Image(Path.getFileURL(Path.rockSprite));
    public static final Image jewelSprite = new Image(Path.getFileURL(Path.jewelSprite));

    static {
        addFrames(metalFrame, 4);
        addFrames(rockFrame, 3);
        addFrames(woodFrame, 2);
        addFrames(jewelFrame, 1);
    }

    private static void addFrames(List<Rectangle2D> list, int count) {
        for (int i = 0; i < count; i++) {
            Rectangle2D rect = new Rectangle2D(x, y + i * height, width, height);
            list.add(rect);
        }
    }
}
