package code_object;

import code_def_path.*;
import code_manager.*;
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
    String effectType;

    public Capsule(String imagePath, String soundPath) {
        super(0, 0, 0, 0);
        image = new Image(Path.getFileURL(imagePath));
        imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 485, 128));
        try {
            sound = new AudioClip(Path.getFileURL(soundPath));
            VolumeManager.registerAudioClip(sound);
        } catch (Exception e) {
            System.err.println("Failed to load capsule sound: " + e.getMessage());
        }
    }

    public void init(double x, double y, int width, int height, double speed, String type) {
        this.speed = speed;
        this.effectType = type;
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        super.setHeight(height);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public double getSpeed() { return speed; }
    public String getEffectType() { return effectType; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public void playSound() {
        if (sound != null) {
            sound.play(VolumeManager.getEffectVolume());
        } else {
            System.err.println("Capsule sound not loaded.");
        }
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