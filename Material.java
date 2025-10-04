import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;

public class Material {
    private Color color;
    private double hardness;
    private double density;

    public Material(Color color, double hardness, double density) {
        this.color = color;
        this.hardness = hardness;
        this.density = density;
    }

    public double getHardness() { return hardness; }

    public double getDensity() {
        return density;
    }

    public Color getColor() { return color; }

    public boolean isEqual(Material other){
        if (!color.equals(other.getColor())) return false;
        if (hardness != other.getHardness()) return false;
        if (density != other.getDensity()) return false;
        return true;
    }

    public static void playSound(Material material_1, Material material_2) {
        if (Constance.priority(material_1) > Constance.priority(material_2)) Constance.play(material_1);
        else Constance.play(material_2);
    }
}
