import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaxiVisualState {
    private static final int WIDTH = 72;
    private static final int HEIGHT = 34;
    private static final double SPEED = 2.4;

    private final Taxi taxi;
    private double x;
    private double y;
    private double initialX;
    private double initialY;
    private boolean selected;
    private List<Point2D.Double> route = new ArrayList<>();
    private int routeIndex;
    private String destinationName = "No route";

    public TaxiVisualState(Taxi taxi, double x, double y) {
        this.taxi = taxi;
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public boolean contains(double pointX, double pointY) {
        return getBounds().contains(pointX, pointY);
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public void storeCurrentPositionAsInitial() {
        this.initialX = x;
        this.initialY = y;
    }

    public void resetToInitialPosition() {
        x = initialX;
        y = initialY;
        routeIndex = 0;
    }

    public boolean updatePosition() {
        if (route.isEmpty() || routeIndex >= route.size()) {
            return true;
        }

        Point2D.Double targetPoint = route.get(routeIndex);
        double dx = targetPoint.x - x;
        double dy = targetPoint.y - y;
        double distance = Math.hypot(dx, dy);

        if (distance < SPEED) {
            x = targetPoint.x;
            y = targetPoint.y;
            routeIndex++;
            return routeIndex >= route.size();
        }

        x += SPEED * dx / distance;
        y += SPEED * dy / distance;
        return false;
    }

    public Color getBodyColor() {
        String colorName = taxi.getColor().trim().toLowerCase();
        return switch (colorName) {
            case "white" -> Color.WHITE;
            case "yellow" -> new Color(255, 214, 10);
            case "green" -> new Color(104, 197, 94);
            case "black" -> new Color(45, 45, 45);
            case "red" -> new Color(215, 68, 68);
            case "blue" -> new Color(86, 132, 255);
            case "gray", "grey" -> new Color(150, 150, 150);
            default -> new Color(255, 200, 80);
        };
    }

    public String getLabel() {
        return taxi.getTaxiId() + " / " + taxi.getDriverName();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setRoute(List<Point2D.Double> routePoints, String destinationName) {
        route = new ArrayList<>(routePoints);
        routeIndex = 0;
        this.destinationName = destinationName;
        if (!route.isEmpty()) {
            x = route.get(0).x;
            y = route.get(0).y;
        }
    }

    public void setRouteMetadata(String startIntersectionName, String destinationName, boolean reverseLane) {
        this.destinationName = destinationName;
    }

    public Point2D.Double getCurrentPosition() {
        return new Point2D.Double(x, y);
    }

    public void chooseRandomTarget(int panelWidth, int panelHeight, Random random) {
        double safeWidth = Math.max(WIDTH + 20, panelWidth - WIDTH - 20);
        double safeHeight = Math.max(HEIGHT + 20, panelHeight - HEIGHT - 20);
        double newTargetX = 10 + random.nextDouble() * (safeWidth - 10);
        double newTargetY = 10 + random.nextDouble() * (safeHeight - 10);
        setRoute(List.of(
                new Point2D.Double(x, y),
                new Point2D.Double(newTargetX, newTargetY)
        ), "Free route");
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
