import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class PolygonFiller extends JPanel {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final int[] xPoints = {100, 300, 450, 350, 250, 150};
    private final int[] yPoints = {200, 200, 100, 350, 150, 350};
    private final int nPoints = xPoints.length;

    private final Color boundaryColor = Color.BLACK;
    private final Color fillColor = new Color(70, 130, 180); // Steel Blue
    private final Color oldColor = Color.WHITE; // Used for Flood Fill
    private final Color backgroundColor = Color.WHITE;

    private BufferedImage buffer;
    private Color currentColor; // Color chosen by the user for filling
    private String currentFillMode = "Outline"; // Default mode

    public PolygonFiller() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        initBuffer();
        currentColor = fillColor;
    }

    private void initBuffer() {
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();

        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(boundaryColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, nPoints);

        g2d.dispose();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer != null) {
            g.drawImage(buffer, 0, 0, this);
            g.setColor(Color.RED);
            g.setFont(new Font("Inter", Font.BOLD, 16));
            g.drawString("Current Mode: " + currentFillMode, 10, 20);
        }
    }

    private void setPixel(int x, int y, Color color) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    private Color getPixel(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            return new Color(buffer.getRGB(x, y));
        }
        return new Color(0, 0, 0, 0); 
    }

    private void scanLineFill(Color fillC) {
        currentFillMode = "Scan-Line Fill (Color: " + getColorName(fillC) + ")";

        int minY = Arrays.stream(yPoints).min().orElse(HEIGHT);
        int maxY = Arrays.stream(yPoints).max().orElse(0);

        for (int y = minY + 1; y < maxY; y++) {
            List<Integer> intersections = new ArrayList<>();

            for (int i = 0; i < nPoints; i++) {
                int j = (i + 1) % nPoints; 

                int x1 = xPoints[i];
                int y1 = yPoints[i];
                int x2 = xPoints[j];
                int y2 = yPoints[j];

                if ((y1 <= y && y2 > y) || (y2 <= y && y1 > y)) {
                    if (y1 != y2) { 
                        int x_intersect = x1 + (int) ((double) (y - y1) * (x2 - x1) / (y2 - y1));
                        intersections.add(x_intersect);
                        if ((y1 < y && y2 > y) || (y2 < y && y1 > y)) {
                        } else if (y1 == y) {
                        } else if (y2 == y) {
                        }
                    }
                }
            }

            Collections.sort(intersections);

            for (int k = 0; k < intersections.size() - 1; k += 2) {
                int xStart = intersections.get(k);
                int xEnd = intersections.get(k + 1);

                for (int x = xStart + 1; x < xEnd; x++) {
                    setPixel(x, y, fillC);
                }
            }
        }

        Graphics2D g2d = buffer.createGraphics();
        g2d.setColor(boundaryColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, nPoints);
        g2d.dispose();

        repaint();
    }

    private void floodFill(int seedX, int seedY, Color newC) {
        currentFillMode = "Flood Fill (Color: " + getColorName(newC) + ")";
        Color oldC = getPixel(seedX, seedY); 

        if (oldC.equals(newC) || oldC.equals(boundaryColor)) return;

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(seedX, seedY));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int x = p.x;
            int y = p.y;

            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                if (getPixel(x, y).equals(oldC)) {
                    setPixel(x, y, newC);
                    stack.push(new Point(x + 1, y));
                    stack.push(new Point(x - 1, y));
                    stack.push(new Point(x, y + 1));
                    stack.push(new Point(x, y - 1));
                }
            }
        }
        repaint();
    }

    private void boundaryFill(int seedX, int seedY, Color fillC, Color boundaryC) {
        currentFillMode = "Seed Fill (Boundary Fill) (Color: " + getColorName(fillC) + ")";

        Queue<Point> queue = new LinkedList<>();
        queue.offer(new Point(seedX, seedY));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                Color currentC = getPixel(x, y);

                if (!currentC.equals(boundaryC) && !currentC.equals(fillC)) {
                    setPixel(x, y, fillC);
                    queue.offer(new Point(x + 1, y));
                    queue.offer(new Point(x - 1, y));
                    queue.offer(new Point(x, y + 1));
                    queue.offer(new Point(x, y - 1));
                }
            }
        }
        repaint();
    }

    private Point calculateCentroid() {
        long sumX = 0;
        long sumY = 0;
        for (int i = 0; i < nPoints; i++) {
            sumX += xPoints[i];
            sumY += yPoints[i];
        }
        int centerX = (int) (sumX / nPoints);
        int centerY = (int) (sumY / nPoints);
        return new Point(centerX, centerY);
    }

    private String getColorName(Color c) {
        if (c.equals(fillColor)) return "Steel Blue";
        if (c.equals(Color.GREEN.darker())) return "Dark Green";
        if (c.equals(Color.ORANGE)) return "Orange";
        return "Custom Color";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Polygon Filling Algorithms Demonstration");
            PolygonFiller fillerPanel = new PolygonFiller();

            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new FlowLayout());

            JButton scanButton = new JButton("1. Scan Fill (Steel Blue)");
            scanButton.addActionListener((ActionEvent e) -> {
                fillerPanel.initBuffer();
                fillerPanel.scanLineFill(fillerPanel.fillColor);
            });

            JButton floodButton = new JButton("2. Flood Fill (Dark Green)");
            floodButton.addActionListener((ActionEvent e) -> {
                fillerPanel.initBuffer();
                Point seed = fillerPanel.calculateCentroid();
                Color newFloodColor = Color.GREEN.darker();
                fillerPanel.floodFill(seed.x, seed.y, newFloodColor);
            });

            JButton seedButton = new JButton("3. Seed Fill (Orange)");
            seedButton.addActionListener((ActionEvent e) -> {
                fillerPanel.initBuffer(); 
                Point seed = fillerPanel.calculateCentroid();
                Color newBoundaryColor = Color.ORANGE;
                fillerPanel.boundaryFill(seed.x, seed.y, newBoundaryColor, fillerPanel.boundaryColor);
            });
            
            JButton resetButton = new JButton("4. Reset Outline");
            resetButton.addActionListener((ActionEvent e) -> {
                fillerPanel.initBuffer();
                fillerPanel.currentFillMode = "Outline";
            });

            menuPanel.add(scanButton);
            menuPanel.add(floodButton);
            menuPanel.add(seedButton);
            menuPanel.add(resetButton);

            frame.setLayout(new BorderLayout());
            frame.add(fillerPanel, BorderLayout.CENTER);
            frame.add(menuPanel, BorderLayout.SOUTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}
