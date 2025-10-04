/* Lớp đại diện cho tấm ván */
public class Paddle extends GameObject{

    public Paddle(double x, double y, int width, int height) {
        super(x, y, width, height);
    }

    public void move(double dx) {
        super.setX(super.getX() + dx);
   }

    @Override
    public void render() {}

}


