public abstract class GameObject {
    private double x;
    private double y;
    private int width;
    private int height;
    private Material material;

    public GameObject(double x, double y, int width, int height, Material material) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.material = material;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setMaterial(Material material) { this.material = material; }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Material getMaterial() { return material; }

    public abstract void render();
}
