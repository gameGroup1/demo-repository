import javax.swing.*;
import javafx.scene.media.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class ImageButton extends JComponent {
    private final Image normalImage;
    private final Image hoveredImage;
    private final String text;
    private final Font baseFont;
    private boolean isHovered = false;
    private ActionListener actionListener;

    // Animation
    private float currentScale = 1.0f;
    private Timer scaleTimer;

    // Kích thước
    private final int targetWidth;
    private final int targetHeight;

    // Constructor
    public ImageButton(Image originalImage, String text, Font font) {
        this.text = text;
        this.baseFont = font.deriveFont(32f); // Font gốc

        int origW = originalImage.getWidth(null);
        int origH = originalImage.getHeight(null);
        float aspectRatio = (float) origW / origH;

        this.targetWidth = 270;
        this.targetHeight = Math.round(targetWidth / aspectRatio);

        this.normalImage = resizeImage(originalImage, targetWidth, targetHeight);
        this.hoveredImage = scaleImage(normalImage, 1.2f);

        setPreferredSize(new Dimension(targetWidth, targetHeight));
        setMinimumSize(new Dimension(targetWidth, targetHeight));
        setMaximumSize(new Dimension(targetWidth, targetHeight));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                animateScale(currentScale, 1.15f);
                playHoverSound();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                animateScale(currentScale, 1.0f);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(ImageButton.this, ActionEvent.ACTION_PERFORMED, text));
                }
            }
        });
    }

    // Animation
    private void animateScale(float from, float to) {
        if (scaleTimer != null && scaleTimer.isRunning()) scaleTimer.stop();

        scaleTimer = new Timer(15, new ActionListener() {
            float current = from;
            float step = (to - from) / 12;
            int frameCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                current += step;
                frameCount++;

                if (frameCount >= 12 || (step > 0 && current >= to) || (step < 0 && current <= to)) {
                    current = to;
                    currentScale = current;
                    repaint();
                    ((Timer) e.getSource()).stop();
                } else {
                    currentScale = current;
                    repaint();
                }
            }
        });
        scaleTimer.start();
    }

    // Resize & Scale image
    private Image resizeImage(Image src, int w, int h) {
        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buf.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(src, 0, 0, w, h, null);
        g2d.dispose();
        return buf;
    }

    private Image scaleImage(Image src, float scale) {
        int w = (int) (src.getWidth(null) * scale);
        int h = (int) (src.getHeight(null) * scale);
        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buf.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(src, 0, 0, w, h, null);
        g2d.dispose();
        return buf;
    }

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    // PAINT: CHỮ SCALE CÙNG NÚT
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // === 1. VẼ ẢNH ===
        int scaledW = (int) (targetWidth * currentScale);
        int scaledH = (int) (targetHeight * currentScale);
        int imgX = (getWidth() - scaledW) / 2;
        int imgY = (getHeight() - scaledH) / 2;

        Image img = isHovered ? hoveredImage : normalImage;
        g2d.drawImage(img, imgX, imgY, scaledW, scaledH, this);

        // === 2. TẠO FONT SCALE ===
        float fontSize = baseFont.getSize() * currentScale;
        Font scaledFont = baseFont.deriveFont(fontSize);

        // Dùng AffineTransform để scale mượt hơn
        AffineTransform at = new AffineTransform();
        at.scale(currentScale, currentScale);
        Font transformedFont = baseFont.deriveFont(at);

        g2d.setFont(transformedFont);

        // === 3. VẼ CHỮ (căn giữa theo scale) ===
        FontMetrics fm = g2d.getFontMetrics(transformedFont);
        int textW = fm.stringWidth(text);
        int textH = fm.getHeight();

        int textX = (getWidth() - textW) / 2;
        int textY = (getHeight() - textH) / 2 + fm.getAscent();

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.drawString(text, textX + 2, textY + 2);

        // Main text
        g2d.setColor(new Color(154, 200, 50));
        g2d.drawString(text, textX, textY);

        g2d.dispose();
    }

    private void playHoverSound() {
        try {
            AudioClip sound = GameMenu.mouseClickSound();
            if (sound != null) sound.play(VolumeManager.getEffectVolume());
        } catch (Exception ignored) {}
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (scaleTimer != null && scaleTimer.isRunning()) scaleTimer.stop();
    }
}