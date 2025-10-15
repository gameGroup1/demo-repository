/* Lớp đại diện cho tấm ván */
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class Paddle extends GameObject {
    private Image image;
    private ImageView imageView;
    private Rectangle2D viewPort;

    public Paddle(double x, double y, int width, int height) {
        super(x, y, width, height);
        image = new Image("file:resources/paddle.png");
        viewPort = new Rectangle2D(0,0,88,22);
        imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setViewport(viewPort);
    }

    public void move(MouseEvent event, Wall leftWall, Wall rightWall) {
        if (event == null || leftWall == null || rightWall == null) return;

        double newX = event.getX();
        double leftBound = leftWall.getX() + leftWall.getWidth();
        double rightBound = rightWall.getX();

        if (newX < leftBound) newX = leftBound;
        else if (newX + getWidth() > rightBound) newX = rightBound - getWidth();

        super.setX(newX);
        imageView.setX(newX);
    }

    @Override
    public void render() {
        imageView.setX(getX());
        imageView.setY(getY());
        imageView.setFitWidth(getWidth());
    }

    public Node getNode() {
        return imageView;
    }
}