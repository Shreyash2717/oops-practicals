import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;

public class PolygonFillCorrected extends JFrame {
    private int[] xPoints = {100, 150, 200, 180, 120};
    private int[] yPoints = {100, 50, 100, 180, 160};
    private int nPoints = xPoints.length;
    private Color fillColor = Color.RED;
    private DrawingPanel panel;

    public PolygonFillCorrected() {
        setTitle("Concave Polygon Fill - Menu Driven");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new DrawingPanel();
        add(panel, BorderLayout.CENTER);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Fill Options");

        JMenuItem floodFillItem = new JMenuItem("Flood Fill");
        floodFillItem.addActionListener(e -> panel.floodFill(150, 100, fillColor));

        JMenuItem seedFillItem = new JMenuItem("Seed Fill");
        seedFillItem.addActionListener(e -> panel.seedFill(150, 100, fillColor));

        JMenuItem scanFillItem = new JMenuItem("Scan Line Fill");
        scanFillItem.addActionListener(e -> panel.scanLineFill(fillColor));

        menu.add(floodFillItem);
        menu.add(seedFillItem);
        menu.add(scanFillItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    class DrawingPanel extends JPanel {
        private BufferedImage canvasImage;

        public DrawingPanel() {
            setBackground(Color.WHITE);
            canvasImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = canvasImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, nPoints);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(canvasImage, 0, 0, null);
        }

        // Flood Fill (recursive)
        public void floodFill(int x, int y, Color replacementColor) {
            int targetColor = canvasImage.getRGB(x, y);
            if (targetColor == replacementColor.getRGB()) return;

            floodFillUtil(x, y, targetColor, replacementColor.getRGB());
            repaint();
        }

        private void floodFillUtil(int x, int y, int targetColor, int replacementColor) {
            if (x < 0 || x >= canvasImage.getWidth() || y < 0 || y >= canvasImage.getHeight())
                return;
            if (canvasImage.getRGB(x, y) != targetColor) return;

            canvasImage.setRGB(x, y, replacementColor);

            floodFillUtil(x + 1, y, targetColor, replacementColor);
            floodFillUtil(x - 1, y, targetColor, replacementColor);
            floodFillUtil(x, y + 1, targetColor, replacementColor);
            floodFillUtil(x, y - 1, targetColor, replacementColor);
        }

        // Seed Fill (stack-based)
        public void seedFill(int x, int y, Color replacementColor) {
            int targetColor = canvasImage.getRGB(x, y);
            if (targetColor == replacementColor.getRGB()) return;

            Stack<Point> stack = new Stack<>();
            stack.push(new Point(x, y));

            while (!stack.isEmpty()) {
                Point p = stack.pop();
                int px = p.x;
                int py = p.y;

                if (px < 0 || px >= canvasImage.getWidth() || py < 0 || py >= canvasImage.getHeight())
                    continue;
                if (canvasImage.getRGB(px, py) != targetColor)
                    continue;

                canvasImage.setRGB(px, py, replacementColor.getRGB());

                stack.push(new Point(px + 1, py));
                stack.push(new Point(px - 1, py));
                stack.push(new Point(px, py + 1));
                stack.push(new Point(px, py - 1));
            }
            repaint();
        }

        // Scan Line Fill
        public void scanLineFill(Color fillColor) {
            int ymin = yPoints[0], ymax = yPoints[0];
            for (int i = 1; i < nPoints; i++) {
                ymin = Math.min(ymin, yPoints[i]);
                ymax = Math.max(ymax, yPoints[i]);
            }

            for (int y = ymin; y <= ymax; y++) {
                ArrayList<Integer> intersections = new ArrayList<>();
                for (int i = 0; i < nPoints; i++) {
                    int x1 = xPoints[i];
                    int y1 = yPoints[i];
                    int x2 = xPoints[(i + 1) % nPoints];
                    int y2 = yPoints[(i + 1) % nPoints];

                    if (y1 == y2) continue; // ignore horizontal edges
                    if ((y >= Math.min(y1, y2)) && (y < Math.max(y1, y2))) {
                        int x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                        intersections.add(x);
                    }
                }
                Collections.sort(intersections);

                for (int i = 0; i < intersections.size() - 1; i += 2) {
                    int xStart = intersections.get(i);
                    int xEnd = intersections.get(i + 1);
                    for (int x = xStart; x <= xEnd; x++) {
                        canvasImage.setRGB(x, y, fillColor.getRGB());
                    }
                }
            }
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PolygonFillCorrected().setVisible(true));
    }
}
