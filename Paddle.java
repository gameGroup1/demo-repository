/* Lớp đại diện cho tấm ván */
public class Paddle {
    public int x, y, width, height;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(int dx) {
        x += dx;
   }
  
}


