import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BilliardTable {
    private static final int PREF_WIDTH = 900;
    private static final int PREF_HEIGHT = 520;
    private static final int RAIL_SIZE = 32;
    private static final int POCKET_RADIUS = 28;

    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public int getInnerLeft() {
        return RAIL_SIZE;
    }

    public int getInnerTop() {
        return RAIL_SIZE;
    }

    public int getInnerRight(int width) {
        return Math.max(getInnerLeft() + 50, width - RAIL_SIZE);
    }

    public int getInnerBottom(int height) {
        return Math.max(getInnerTop() + 50, height - RAIL_SIZE);
    }

    public boolean isInPocket(double centerX, double centerY, int width, int height) {
        for (Point2D.Double pocketCenter : getPocketCenters(width, height)) {
            if (pocketCenter.distance(centerX, centerY) <= POCKET_RADIUS) {
                return true;
            }
        }
        return false;
    }

    public void paint(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(92, 55, 26));
        g2.fillRoundRect(0, 0, width, height, 22, 22);

        int innerWidth = getInnerRight(width) - getInnerLeft();
        int innerHeight = getInnerBottom(height) - getInnerTop();
        g2.setColor(new Color(22, 122, 58));
        g2.fillRoundRect(getInnerLeft(), getInnerTop(), innerWidth, innerHeight, 18, 18);

        g2.setColor(new Color(232, 220, 178));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(getInnerLeft(), getInnerTop(), innerWidth, innerHeight, 18, 18);

        g2.setColor(Color.BLACK);
        for (Point2D.Double pocketCenter : getPocketCenters(width, height)) {
            g2.fillOval((int) pocketCenter.x - POCKET_RADIUS, (int) pocketCenter.y - POCKET_RADIUS,
                    POCKET_RADIUS * 2, POCKET_RADIUS * 2);
        }
    }

    private List<Point2D.Double> getPocketCenters(int width, int height) {
        List<Point2D.Double> pockets = new ArrayList<>();
        int left = getInnerLeft();
        int right = getInnerRight(width);
        int top = getInnerTop();
        int bottom = getInnerBottom(height);
        int middleX = left + (right - left) / 2;

        pockets.add(new Point2D.Double(left, top));
        pockets.add(new Point2D.Double(middleX, top));
        pockets.add(new Point2D.Double(right, top));
        pockets.add(new Point2D.Double(left, bottom));
        pockets.add(new Point2D.Double(middleX, bottom));
        pockets.add(new Point2D.Double(right, bottom));
        return pockets;
    }
}
