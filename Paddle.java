/* Lớp đại diện cho tấm ván */
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class Paddle extends GameObject {
    private Rectangle rect;

    public Paddle(double x, double y, int width, int height, Material material) {
        super(x, y, width, height, material);
        rect = new Rectangle(x, y, width, height);
        rect.setFill(material.getColor());
    }

    public void move(MouseEvent event, Wall leftWall, Wall rightWall) {
        if (event == null || leftWall == null || rightWall == null) return;

        double newX = event.getX();
        double leftBound = leftWall.getX() + leftWall.getWidth();
        double rightBound = rightWall.getX();

        if (newX < leftBound) newX = leftBound;
        else if (newX + getWidth() > rightBound) newX = rightBound - getWidth();

        super.setX(newX);
        rect.setX(newX);
    }

    @Override
    public void render() {
        rect.setX(getX());
        rect.setY(getY());
        rect.setWidth(getWidth());
        rect.setHeight(getHeight());
        if (getMaterial() != null) {
            rect.setFill(getMaterial().getColor());
        }
    }

    public Node getNode() {
        return rect;
    }
}