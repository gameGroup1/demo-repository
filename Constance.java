import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;

public class Constance {
    public static final Material wood = new Material(Color.BURLYWOOD, 5.0, 0.6);
    public static final Material rock = new Material(Color.BROWN, 3.0, 1.2);
    public static final Material metal = new Material(Color.STEELBLUE, 10.0, 7.8);
    public static final Material jewel = new Material(Color.CRIMSON, 2.0, 3.5);
    public static final AudioClip woodSound = new AudioClip("file:sound and music/wood_break.mp3");
    public static final AudioClip rockSound = new AudioClip("file:sound and music/rock_break.mp3");
    public static final AudioClip metalSound = new AudioClip("file:sound and music/metal_break.mp3");
    public static final AudioClip jewelSound = new AudioClip("file:sound and music/jewel_break.mp3");
    
    public static int priority(Material material) {
        if (material == jewel) return 4;
        if (material == wood) return 3;
        if (material == rock) return 2;
        return 1;
    }

    public static void play(Material material) {
        if (material.isEqual(wood)) woodSound.play();
        if (material.isEqual(rock)) rockSound.play();
        if (material.isEqual(metal)) metalSound.play();
        if (material.isEqual(jewel)) jewelSound.play();
    }
}
