/* Lớp đại diện cho quả bóng */
public class Ball extends GameObject{
     private double dx, dy;
     private double radius, speed;

     public Ball(double x, double y, double radius, double speed, Material material) {
          super(x,y,material);
          this.radius = radius;
          this.speed = speed;
     }

     public double getRadius() { return radius; }
     public double getSpeed() { return speed; }
     public double getDx() { return dx; }
     public double getDy() { return dy; }

     public void setDx(double dx) { this.dx = dx; }
     public void setDy(double dy) { this.dy = dy; }

    @Override
    public void render() {}
}
