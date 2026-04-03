import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Ball {
    private double x;
    private double y;
    private double dx;
    private double dy;
    private final int diameter;
    private final Color color;
    private volatile boolean pocketed;

    public Ball(double x, double y, double dx, double dy, int diameter, Color color) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.diameter = diameter;
        this.color = color;
    }

    public synchronized void draw(Graphics2D g2) {
        if (pocketed) {
            return;
        }

        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));
        g2.setColor(Color.WHITE);
        g2.draw(new Ellipse2D.Double(x, y, diameter, diameter));
    }

    public synchronized boolean move(BilliardTable table, int canvasWidth, int canvasHeight) {
        if (pocketed || canvasWidth <= 0 || canvasHeight <= 0) {
            return pocketed;
        }
        x += dx;
        y += dy;

        double centerX = x + diameter / 2.0;
        double centerY = y + diameter / 2.0;
        if (table.isInPocket(centerX, centerY, canvasWidth, canvasHeight)) {
            pocketed = true;
            return true;
        }

        double left = table.getInnerLeft();
        double right = table.getInnerRight(canvasWidth) - diameter;
        double top = table.getInnerTop();
        double bottom = table.getInnerBottom(canvasHeight) - diameter;

        if (x < left) {
            x = left;
            dx = -dx;
        }
        if (x > right) {
            x = right;
            dx = -dx;
        }
        if (y < top) {
            y = top;
            dy = -dy;
        }
        if (y > bottom) {
            y = bottom;
            dy = -dy;
        }

        return false;
    }

    public synchronized double getX() {
        return x;
    }

    public synchronized double getY() {
        return y;
    }

    public int getDiameter() {
        return diameter;
    }

    public boolean isPocketed() {
        return pocketed;
    }
}
