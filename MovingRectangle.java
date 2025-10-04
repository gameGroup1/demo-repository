import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MovingRectangle extends JFrame {
    private int rectX = 50;
    private int rectY = 50;
    private final int RECT_WIDTH = 50;
    private final int RECT_HEIGHT = 30;
    private final int moveStep = 5;

    public MovingRectangle() {
        super("Hình chữ nhật chuyển động theo chuột");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawingPanel panel = new DrawingPanel();
        panel.setPreferredSize(new Dimension(400, 400));
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel() {
            setFocusable(true);

            // Mouse move: cập nhật toạ độ (căn giữa)
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    rectX = e.getX() - RECT_WIDTH / 2;
                    rectY = e.getY() - RECT_HEIGHT / 2;
                    clampToBounds();
                    repaint();
                }
            });

            // Key bindings (hoạt động tốt khi cửa sổ có focus)
            InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();

            im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
            im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
            im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
            im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");

            am.put("moveUp", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    rectY -= moveStep;
                    clampToBounds();
                    repaint();
                }
            });
            am.put("moveDown", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    rectY += moveStep;
                    clampToBounds();
                    repaint();
                }
            });
            am.put("moveLeft", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    rectX -= moveStep;
                    clampToBounds();
                    repaint();
                }
            });
            am.put("moveRight", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    rectX += moveStep;
                    clampToBounds();
                    repaint();
                }
            });
        }

        private void clampToBounds() {
            if (rectX < 0) rectX = 0;
            if (rectY < 0) rectY = 0;
            if (rectX + RECT_WIDTH > getWidth()) rectX = getWidth() - RECT_WIDTH;
            if (rectY + RECT_HEIGHT > getHeight()) rectY = getHeight() - RECT_HEIGHT;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillRect(rectX, rectY, RECT_WIDTH, RECT_HEIGHT);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovingRectangle win = new MovingRectangle();
            win.setVisible(true);
        });
    }
}
