/*import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;

public enum Material {
    wood(Color.BURLYWOOD, 2.0, Path.woodSound),
    rock(Color.BROWN, 3.0, Path.rockSound),
    metal(Color.STEELBLUE, 4.0, Path.metalSound),
    jewel(Color.CRIMSON, 1.0, Path.jewelSound);

    private final Color color;
    private final double hardness;
    private final AudioClip sound;

    // Khởi tạo với xử lý lỗi (phần 4.1.1)
    Material(Color color, double hardness, String soundPath) {
        this.color = color;
        this.hardness = hardness;
        AudioClip tempSound = null;
        try {
            tempSound = new AudioClip(Path.getFileURL(soundPath));
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

    public boolean equals(Material other) {
        if(!color.equals(other.color)) return false;
        if(hardness != other.hardness) return false;
        if(!sound.equals(other.sound)) return false;
        return true;
    }

    public double getHardness() {
        return hardness;
    }

    public Color getColor() {
        return color;
    }

    public int priority() {
        if(this.equals(jewel)) return 3;
        if(this.equals(wood)) return 2;
        if(this.equals(rock)) return 1;
        return 0;
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
}*/
