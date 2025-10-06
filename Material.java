import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;

public class Material {
    private static AudioClip woodSound;
    private static AudioClip rockSound;
    private static AudioClip metalSound;
    private static AudioClip jewelSound;

    // Các hằng số static final (singleton-like)
    public static final Material wood = new Material(Color.BURLYWOOD, 2.0);
    public static final Material rock = new Material(Color.BROWN, 3.0);
    public static final Material metal = new Material(Color.STEELBLUE, 4.0);
    public static final Material jewel = new Material(Color.CRIMSON, 1.0);

    private Color color;
    private double hardness;

    // Constructor
    public Material(Color color, double hardness) {
        this.color = color;
        this.hardness = hardness;
    }

    // ✅ Hàm khởi tạo âm thanh — gọi sau khi JavaFX Application đã khởi động
    public static void initSounds() {
        try {
            woodSound = new AudioClip(getFileURL("/sound_and_music/wood_break.mp3"));
            rockSound = new AudioClip(getFileURL("/sound_and_music/rock_break.mp3"));
            metalSound = new AudioClip(getFileURL("/sound_and_music/metal_break.mp3"));
            jewelSound = new AudioClip(getFileURL("/sound_and_music/jewel_break.mp3"));
            System.out.println("All sounds loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    public double getHardness() {
        return hardness;
    }

    public Color getColor() {
        return color;
    }

    public boolean isEqual(Material other) {
        if (other == null) return false;
        return color.equals(other.getColor()) && hardness == other.getHardness();
    }

    public int priority() {
        if (this.isEqual(jewel)) return 4;
        if (this.isEqual(wood)) return 3;
        if (this.isEqual(rock)) return 2;
        return 1;
    }

    public void play() {
        try {
            if (this.isEqual(jewel) && jewelSound != null) jewelSound.play();
            else if (this.isEqual(wood) && woodSound != null) woodSound.play();
            else if (this.isEqual(rock) && rockSound != null) rockSound.play();
            else if (this.isEqual(metal) && metalSound != null) metalSound.play();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void playSound(Material m1, Material m2) {
        if (m1 == null || m2 == null) return;
        if (m1.priority() > m2.priority()) m1.play();
        else m2.play();
    }

    private static String getFileURL(String relativePath) {
        String basePath = System.getProperty("user.dir").replace("\\", "/");
        return "file:///" + basePath + relativePath;
    }
}
