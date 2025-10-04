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
        private boolean left, right, up, down;
        private Timer timer;

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

            im.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
            im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
            im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
            im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
            im.put(KeyStroke.getKeyStroke("pressed UP"), "upPressed");
            im.put(KeyStroke.getKeyStroke("released UP"), "upReleased");
            im.put(KeyStroke.getKeyStroke("pressed DOWN"), "downPressed");
            im.put(KeyStroke.getKeyStroke("released DOWN"), "downReleased");

            am.put("leftPressed", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    left = true;
                }
            });

            am.put("leftReleased", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    left = false;
                }
            });

            am.put("rightPressed", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    right = true;
                }
            });

            am.put("rightReleased", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    right = false;
                }
            });

            am.put("upPressed", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    up = true; }
            });

            am.put("upReleased", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    up = false; }
            });

            am.put("downPressed", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    down = true;
                }
            });

            am.put("downReleased", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    down = false;
                }
            });

            // Timer: chạy đều, di chuyển khi flag true
            timer = new Timer(15, ev -> { // ~66 FPS
                boolean moved = false;
                if (left)  { rectX -= moveStep; moved = true; }
                if (right) { rectX += moveStep; moved = true; }
                if (up)    { rectY -= moveStep; moved = true; }
                if (down)  { rectY += moveStep; moved = true; }
                if (moved) {
                    clampToBounds();
                    repaint();
                }
            });
            timer.start();
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
