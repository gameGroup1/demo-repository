import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

public class Frames {
    private static final int x = 0;
    private static final int y = 0;
    private static final int width = 384;
    private static final int height = 128;
    public static final List<Rectangle2D> woodBrick = new ArrayList<>();
    public static final List<Rectangle2D> metalBrick = new ArrayList<>();
    public static final List<Rectangle2D> rockBrick = new ArrayList<>();
    public static final List<Rectangle2D> jewelBrick = new ArrayList<>();
    public static final Image woodSprite = new Image(getFileURL("/Sprite_Bricks/Wood/sprite.png"));
    public static final Image metalSprite = new Image(getFileURL("/Sprite_Bricks/Metal/sprite.png"));
    public static final Image rockSprite = new Image(getFileURL("/Sprite_Bricks/Rock/sprite.png"));
    public static final Image jewelSprite = new Image(getFileURL("/Sprite_Bricks/Jewel/sprite.png"));

    static {
        addFrames(metalBrick, 4);
        addFrames(rockBrick, 3);
        addFrames(woodBrick, 2);
        addFrames(jewelBrick, 1);
    }

    private static void addFrames(List<Rectangle2D> list, int count) {
        for (int i = 0; i < count; i++) {
            Rectangle2D rect = new Rectangle2D(x, y + i * height, width, height);
            list.add(rect);
        }
    }

    public static String getFileURL(String relativePath) {
        return "file:///" + System.getProperty("user.dir").replace("\\", "/") + relativePath;
    }
}
