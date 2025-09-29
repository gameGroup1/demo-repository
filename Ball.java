public class Ball {
    public int x, y, radius;
    Ball(int x, int y, int radius){
         this.x = x;
         this.y = y;
         this.radius =radius;
    }
    public void move(int dx, int dy){
         x += dx;
         y += dy;
    }
}
