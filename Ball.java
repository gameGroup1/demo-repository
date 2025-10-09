/* Lớp đại diện cho quả bóng */
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
     private Image image;
     private boolean visible;// Ảnh sprite sheet duy nhất chứa tất cả frames

     public Ball(double x, double y, double radius, double speed, Material material) {
          super(x,y,(int) radius * 2, (int) radius * 2, material);
          this.radius = radius;
          this.speed = speed;
          this.power = 1;
          this.visible = true;
          image = new Image("ball.png");
          imageView = new ImageView(image);
          imageView.setViewport(new Rectangle2D(0,0,128, 128));
          imageView.setFitWidth(radius * 2);
          imageView.setFitHeight(radius * 2);
     }

     public double getRadius() { return radius; }
     public double getSpeed() { return speed; }
     public double getDx() { return dx; }
     public double getDy() { return dy; }
     public int getPower() { return power; }
     public boolean isVisible() { return visible; }

     public void setDx(double dx) { this.dx = dx; }
     public void setDy(double dy) { this.dy = dy; }
     public void setPower(int power) { this.power = power; }
     public void setSpeed(double speed) { this.speed = speed; }
     public void setVisible(boolean visible) { this.visible = visible; }

    @Override
    public void render() {
        if (imageView != null) {
            imageView.setX(getX() - radius);
            imageView.setY(getY() - radius);
            if (visible) {
                imageView.setVisible(true);
            } else {
                imageView.setVisible(false);
            }
        }
    }

    public Node getNode() {
        return imageView;
    }
}
