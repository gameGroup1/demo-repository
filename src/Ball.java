/* Lớp đại diện cho quả bóng */
import javafx.scene.shape.Circle;
import javafx.scene.Node;

public class Ball extends GameObject{
     private double dx, dy;
     private double radius;
     private double speed;
     private int power;
     private Circle circle;

     public Ball(double x, double y, double radius, double speed, Material material) {
          super(x,y,material);
          this.radius = radius;
          this.speed = speed;
          this.power = 1;
          this.circle = new Circle(x, y, radius, material.getColor());
     }

     public double getRadius() { return radius; }
     public double getSpeed() { return speed; }
     public double getDx() { return dx; }
     public double getDy() { return dy; }
     public int getPower() { return power; }

     public void setSpeed(double speed) { this.speed = speed; }
     public void setDx(double dx) { this.dx = dx; }
     public void setDy(double dy) { this.dy = dy; }
     public void setPower(int power) { this.power = power; }

     public void move() {
        // Cập nhật vị trí từ GameObject (kế thừa)
        setX(getX() + dx);
        setY(getY() + dy);

        // Đồng bộ với Circle trong JavaFX scene graph (tích hợp GUI - phần 4.2.1)
        if (circle != null) {
            circle.setCenterX(getX() + radius); // Căn giữa tâm Circle với vị trí bóng
            circle.setCenterY(getY() + radius);
        }
    }

    @Override
    public void render() {
        if (circle != null) {
            circle.setCenterX(getX() + radius);
            circle.setCenterY(getY() + radius);
            circle.setRadius(radius);
            if (getMaterial() != null) {
                circle.setFill(getMaterial().getColor()); // Cập nhật màu từ Material (hiệu ứng hình ảnh - phần 4.2.2)
            }
        }
    }

    public Node getNode() {
        return circle;
    }
}
