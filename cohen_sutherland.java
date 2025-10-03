import java.awt.*;
import java.awt.event.*;

class CohenSutherlandLineClippingAWT extends Frame {

    // Cohen-Sutherland region codes
    static final int INSIDE = 0; // 0000
    static final int LEFT = 1;   // 0001
    static final int RIGHT = 2;  // 0010
    static final int BOTTOM = 4; // 0100
    static final int TOP = 8;    // 1000

    // Clipping window coordinates
    static final double xMin = 100, yMin = 100; // Increased for better visibility in a typical window
    static final double xMax = 400, yMax = 300;

    // Structure to hold line segments for drawing
    static class LineSegment {
        double x1, y1, x2, y2;
        boolean accepted;

        LineSegment(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.accepted = false;
        }

        // Method to perform Cohen-Sutherland Clipping on this segment
        public void clip() {
            cohenSutherlandClip(this);
        }
    }

    private static int computeCode(double x, double y) {
        int code = INSIDE;

        if (x < xMin) {
            code |= LEFT;
        } else if (x > xMax) {
            code |= RIGHT;
        }
        if (y < yMin) {
            code |= BOTTOM;
        } else if (y > yMax) {
            code |= TOP;
        }
        return code;
    }

    public static void cohenSutherlandClip(LineSegment line) {
        double x1 = line.x1;
        double y1 = line.y1;
        double x2 = line.x2;
        double y2 = line.y2;

        int code1 = computeCode(x1, y1);
        int code2 = computeCode(x2, y2);

        boolean accept = false;

        while (true) {
            if ((code1 == 0) && (code2 == 0)) {
                accept = true;
                break;
            } else if ((code1 & code2) != 0) {
                break;
            } else {
                int codeOut;
                double x = 0, y = 0;

                if (code1 != 0) {
                    codeOut = code1;
                } else {
                    codeOut = code2;
                }

                // Calculate intersection point
                if ((codeOut & TOP) != 0) {
                    x = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
                    y = yMax;
                } else if ((codeOut & BOTTOM) != 0) {
                    x = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
                    y = yMin;
                } else if ((codeOut & RIGHT) != 0) {
                    y = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
                    x = xMax;
                } else if ((codeOut & LEFT) != 0) {
                    y = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
                    x = xMin;
                }

                // Replace point with intersection
                if (codeOut == code1) {
                    x1 = x;
                    y1 = y;
                    code1 = computeCode(x1, y1);
                } else {
                    x2 = x;
                    y2 = y;
                    code2 = computeCode(x2, y2);
                }
            }
        }

        if (accept) {
            line.accepted = true;
            line.x1 = x1;
            line.y1 = y1;
            line.x2 = x2;
            line.y2 = y2;
            System.out.println("Line accepted from (" + (int)x1 + ", " + (int)y1 + ") to (" + (int)x2 + ", " + (int)y2 + ")");
        } else {
            line.accepted = false;
            System.out.println("Line rejected");
        }
    }

    // Array to store the lines to be clipped and drawn
    private LineSegment[] lines;

    public CohenSutherlandLineClippingAWT() {
        super("Cohen-Sutherland Line Clipping");
        setSize(500, 400); // Set initial window size
        setBackground(Color.WHITE);

        // Define and clip the lines (using the example lines from the original main)
        LineSegment line1 = new LineSegment(50, 50, 450, 350); // Crosses multiple boundaries
        LineSegment line2 = new LineSegment(150, 150, 350, 250); // Fully inside
        LineSegment line3 = new LineSegment(50, 350, 50, 50); // Fully outside, to the left
        LineSegment line4 = new LineSegment(300, 50, 150, 350); // Crosses bottom and top

        line1.clip();
        line2.clip();
        line3.clip();
        line4.clip();

        lines = new LineSegment[]{line1, line2, line3, line4};

        // Add a window listener to handle closing the frame
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        setVisible(true); // Make the frame visible
    }

    // AWT method for drawing
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 1. Draw the clipping window
        g.setColor(Color.RED);
        // Use casting to int for drawing coordinates
        g.drawRect((int) xMin, (int) yMin, (int) (xMax - xMin), (int) (yMax - yMin));
        g.drawString("Clipping Window", (int) xMin, (int) yMin - 10);

        // 2. Draw the clipped lines
        for (LineSegment line : lines) {
            if (line.accepted) {
                g.setColor(Color.BLUE);
                // Draw the clipped line segment
                g.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);

                // Optional: Highlight the endpoints of the clipped line
                g.setColor(Color.GREEN.darker());
                g.fillOval((int) line.x1 - 3, (int) line.y1 - 3, 6, 6);
                g.fillOval((int) line.x2 - 3, (int) line.y2 - 3, 6, 6);
            }
        }
    }

    public static void main(String[] args) {
        // Run the AWT application
        new CohenSutherlandLineClippingAWT();
    }
}