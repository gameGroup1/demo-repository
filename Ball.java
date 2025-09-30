/* Lớp đại diện cho quả bóng */
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
}
