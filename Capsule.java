import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.Node;

public class Capsule extends GameObject {
    private Image image;
    private AudioClip sound;
    private ImageView imageView;
    private double speed;
    private boolean visible = false;

    Capsule(String imagePath, String soundPath) {
        super(0, 0, 0, 0, null); // Vật liệu không quan trọng ở đây
        image = new Image(Path.getFileURL(imagePath));
        imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 485, 128));
        try {
            sound = new AudioClip(Path.getFileURL(soundPath));
            sound.play(0.0); // Preload với volume 0
        } catch (Exception e) {
            System.err.println("Failed to load capsule sound: " + e.getMessage());
        }
    }

    public void init(double x, double y, int width, int height, double speed) {
        this.speed = speed;
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        super.setHeight(height);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public boolean equals(Capsule other) {
        if (getWidth() != other.getWidth()) return false;
        if (getHeight() != other.getHeight()) return false;
        if (speed != other.getSpeed()) return false;
        if (!image.equals(other.image)) return false;
        if (!sound.equals(other.sound)) return false;
        return true;
    }

    public double getSpeed() { return speed; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void playSound() {
        if (sound != null) sound.play();
        else System.err.println("Capsule sound not loaded.");
    }

    @Override
    public void render() {
        if (imageView != null) {
            imageView.setY(getY());
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
