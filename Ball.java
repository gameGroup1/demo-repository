import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

public class Ball extends GameObject{
    private double dx, dy;
    private double radius;
    private double speed;
    private int power;
    private ImageView imageView; // Đối tượng JavaFX để hiển thị hình ảnh
    private Image normalImage;
    private Image fireImage;
    private Rectangle2D normalViewport;
    private Rectangle2D fireViewport;
    private boolean isFireBall = false;

    public Ball(double x, double y, double radius, double speed) {
        super(x, y,(int) radius * 2, (int) radius * 2);
        this.radius = radius;
        this.speed = speed;
        this.power = 1;
        normalImage = new Image("file:resources/ball.png");
        fireImage = new Image("file:resources/fireball.gif");
        imageView = new ImageView(normalImage);
        normalViewport = new Rectangle2D(0, 0, 128, 128);
        fireViewport = new Rectangle2D(33, 22, 55, 55);
        imageView.setViewport(normalViewport);
        imageView.setFitWidth(radius * 2);
        imageView.setFitHeight(radius * 2);
    }

    public double getRadius() { return radius; }
    public double getSpeed() { return speed; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getPower() { return power; }

    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public void setPower(int power) { this.power = power; }
    public void setSpeed(double speed) { this.speed = speed; }

    public boolean isFireBall() {
        return isFireBall; // Trả về biến boolean trực tiếp
    }
    
    public void setFireBall(boolean isFireBall) {
        this.isFireBall = isFireBall; // Lưu trạng thái
        if (isFireBall) {
            imageView.setImage(fireImage);
            imageView.setViewport(fireViewport);
        }
        else {
            imageView.setImage(normalImage);
            imageView.setViewport(normalViewport);
        }
    }

    @Override
    public void render() {
        if (imageView != null) {
            imageView.setX(getX() - radius);
            imageView.setY(getY() - radius);
        }
    }

    public Node getNode() {
        return imageView;
    }
}