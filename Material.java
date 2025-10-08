import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import jdk.jshell.spi.SPIResolutionException;

public class Material {
    private static AudioClip woodSound;
    private static AudioClip rockSound;
    private static AudioClip metalSound;
    private static AudioClip jewelSound;

    // Static block to initialize AudioClips with status notifications (phần 4.1.1: xử lý lỗi load âm thanh)
    static {
        try {
            woodSound = new AudioClip(getFileURL("/sound_and_music/wood_break.mp3"));
            System.out.println("Loaded wood sound successfully."); // Thông báo trạng thái load thành công
        } catch (Exception e) {
            System.err.println("Failed to load wood sound: " + e.getMessage());
            woodSound = null; // Đặt null nếu lỗi
        }

        try {
            rockSound = new AudioClip(getFileURL("/sound_and_music/rock_break.mp3"));
            System.out.println("Loaded rock sound successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load rock sound: " + e.getMessage());
            rockSound = null;
        }

        try {
            metalSound = new AudioClip(getFileURL("/sound_and_music/metal_break.mp3"));
            System.out.println("Loaded metal sound successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load metal sound: " + e.getMessage());
            metalSound = null;
        }

        try {
            jewelSound = new AudioClip(getFileURL("/sound_and_music/jewel_break.mp3"));
            System.out.println("Loaded jewel sound successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load jewel sound: " + e.getMessage());
            jewelSound = null;
        }
    }

    private void preloadSounds() {
        if (woodSound != null) woodSound.play(0.0); // Phát với volume 0 để init mà không nghe tiếng
        if (rockSound != null) rockSound.play(0.0);
        if (metalSound != null) metalSound.play(0.0);
        if (jewelSound != null) jewelSound.play(0.0);
        System.out.println("Preloaded all sounds.");
    }

    // Các hằng số static final (singleton-like, phần 5.2: đóng gói toàn cục)
    public static final Material wood = new Material(Color.BURLYWOOD, 2.0);
    public static final Material rock = new Material(Color.BROWN, 3.0);
    public static final Material metal = new Material(Color.STEELBLUE, 4.0);
    public static final Material jewel = new Material(Color.CRIMSON, 1.0);

    private Color color; // Màu sắc (private, đóng gói - phần 5.2)
    private double hardness; // Độ cứng (private, đóng gói - phần 5.2)

    public Material(Color color, double hardness) {
        this.color = color;
        this.hardness = hardness;
        preloadSounds();
    }

    // Getter cho hardness (sử dụng trong va chạm, phần 4.1.1)
    public double getHardness() {
        return hardness;
    }

    // Getter cho color (sử dụng trong render - phần 4.2.1)
    public Color getColor() {
        return color;
    }

    public boolean isEqual(Material other) {
        if (other == null) return false; // Xử lý lỗi null (phần 4.1.1)
        if (!color.equals(other.getColor())) return false;
        if (hardness != other.getHardness()) return false;
        return true;
    }

    public int priority() {
        if (this.isEqual(jewel)) return 4;
        if (this.isEqual(wood)) return 3;
        if (this.isEqual(rock)) return 2;
        return 1;
    }

    public void play() {
        try {
            if (this.isEqual(jewel)) {
                if (jewelSound != null) {
                    jewelSound.play();
                } else {
                    System.err.println("Jewel sound not loaded, skipping play.");
                }
            } else if (this.isEqual(wood)) {
                if (woodSound != null) {
                    woodSound.play();
                } else {
                    System.err.println("Wood sound not loaded, skipping play.");
                }
            } else if (this.isEqual(rock)) {
                if (rockSound != null) {
                    rockSound.play();
                } else {
                    System.err.println("Rock sound not loaded, skipping play.");
                }
            } else if (this.isEqual(metal)) {
                if (metalSound != null) {
                    metalSound.play();
                } else {
                    System.err.println("Metal sound not loaded, skipping play.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void playSound(Material material_1, Material material_2) {
        if (material_1 == null || material_2 == null) return; // Xử lý lỗi null
        if (material_1.priority() > material_2.priority()) {
            material_1.play();
        } else {
            material_2.play();
        }
    }

    private static String getFileURL(String relativePath) {
        String basePath = System.getProperty("user.dir").replace("\\", "/");
        return "file:///" + basePath + relativePath;
    }
}