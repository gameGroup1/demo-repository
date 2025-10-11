/* Lớp đại diện cho tấm ván */
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Paddle extends GameObject {
    private Rectangle rect;
    private Color color;

    public Paddle(double x, double y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color;
        rect = new Rectangle(x, y, width, height);
        rect.setFill(color);
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
    }

    public Node getNode() {
        return rect;
    }
}