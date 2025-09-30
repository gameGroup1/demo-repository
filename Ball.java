public class Ball {
     public int x, y, radius, speed;
     public double dx, dy;

     public Ball(int x, int y, int radius, int speed){
          this.x = x;
          this.y = y;
          this.radius = radius;
          this.speed = speed;
          this.dx = -1;
          this.dy = -1;
     }


     public void move() {
          x += dx * speed;
          y += dy * speed;
     }

     public void move(int dx, int dy){
          this.x += dx * speed;
          this.y += dy * speed;
     }

     public void resetPosition(int x, int y) {
          this.x = x;
          this.y = y;
     }

     public void reverseDirection() {
          this.dx = -this.dx;
          this.dy = -this.dy;
     }
}
