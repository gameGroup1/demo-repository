import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;

public enum Material {
    wood(Color.BURLYWOOD, 2.0, "/sound_and_music/wood_break.mp3"),
    rock(Color.BROWN, 3.0, "/sound_and_music/rock_break.mp3"),
    metal(Color.STEELBLUE, 4.0, "/sound_and_music/metal_break.mp3"),
    jewel(Color.CRIMSON, 1.0, "/sound_and_music/jewel_break.mp3");

    private final Color color;
    private final double hardness;
    private final AudioClip sound;

    // Khởi tạo với xử lý lỗi (phần 4.1.1)
    Material(Color color, double hardness, String soundPath) {
        this.color = color;
        this.hardness = hardness;
        AudioClip tempSound = null;
        try {
            tempSound = new AudioClip(Frames.getFileURL(soundPath));
            tempSound.play(0.0); // Preload với volume 0
        } catch (Exception e) {
            System.err.println("Failed to load sound for " + this.name() + ": " + e.getMessage());
        }
        this.sound = tempSound;
    }

    public static void preloadSounds() {
        wood.sound.play(0.0);
        rock.sound.play(0.0);
        metal.sound.play(0.0);
        jewel.sound.play(0.0);
    }

    public double getHardness() {
        return hardness;
    }

    public Color getColor() {
        return color;
    }

    public int priority() {
        return switch (this) {
            case jewel -> 4;
            case wood -> 3;
            case rock -> 2;
            default -> 1;
        };
    }

    public void play() {
        if (sound != null) sound.play();
        else {
            System.err.println(this.name() + " sound not loaded.");
        }
    }

    public static void playSound(Material mat1, Material mat2) {
        if (mat1 == null || mat2 == null) return;
        (mat1.priority() > mat2.priority() ? mat1 : mat2).play();
    }
}