/* Lớp đại diện cho quả bóng */
public class Ball {
     private double x, y, radius, speed;
     private double dx, dy;

     public Ball(double x, double y, double radius, double speed) {
          this.x = x;
          this.y = y;
          this.radius = radius;
          this.speed = speed;
     }

     public void setX(double x) {
          this.x = x;
     }

     public void setY(double y) {
          this.y = y;
     }

     public void setRadius(double radius) {
          this.radius = radius;
     }

     public void setSpeed(double speed) {
          this.speed = speed;
     }

     
     public void setDx(double dx) {
          this.dx = dx;
     }

     public void setDy(double dy) {
          this.dy = dy;
     }

     public double getX() {
          return x;
     }

     public double getY() {
          return y;
     }

     public double getRadius() {
          return radius;
     }

     public double getSpeed() {
          return speed;
     }

     public double getDx() {
          return dx;
     }

     public double getDy() {
          return dy;
     }

     
}
