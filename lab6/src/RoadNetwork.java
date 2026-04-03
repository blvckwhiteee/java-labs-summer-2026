import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class RoadNetwork {
    private final Map<String, Point2D.Double> intersections = new LinkedHashMap<>();
    private final Map<String, List<String>> adjacency = new HashMap<>();
    private final List<RoadSegment> roadSegments = new ArrayList<>();

    public RoadNetwork() {
        addIntersection("DEPOT", 80, 120);
        addIntersection("WEST_A", 250, 120);
        addIntersection("CENTER_A", 460, 120);
        addIntersection("EAST_A", 700, 120);

        addIntersection("WEST_B", 250, 230);
        addIntersection("CENTER_B", 460, 230);
        addIntersection("EAST_B", 700, 230);

        addIntersection("SOUTH_W", 250, 340);
        addIntersection("SOUTH_C", 460, 340);
        addIntersection("AIRPORT", 700, 340);

        connectTwoWay("DEPOT", "WEST_A");
        connectTwoWay("WEST_A", "CENTER_A");
        connectTwoWay("CENTER_A", "EAST_A");

        connectTwoWay("WEST_B", "CENTER_B");
        connectTwoWay("CENTER_B", "EAST_B");

        connectTwoWay("SOUTH_W", "SOUTH_C");
        connectTwoWay("SOUTH_C", "AIRPORT");

        connectTwoWay("WEST_A", "WEST_B");
        connectTwoWay("WEST_B", "SOUTH_W");

        connectTwoWay("CENTER_A", "CENTER_B");
        connectTwoWay("CENTER_B", "SOUTH_C");

        connectTwoWay("EAST_A", "EAST_B");
        connectTwoWay("EAST_B", "AIRPORT");

        connectTwoWay("WEST_A", "CENTER_B");
        connectTwoWay("CENTER_B", "EAST_A");
        connectTwoWay("CENTER_A", "EAST_B");
    }

    public Point2D.Double getRandomSpawnPoint(Random random) {
        List<String> keys = new ArrayList<>(intersections.keySet());
        return copy(intersections.get(keys.get(random.nextInt(keys.size()))));
    }

    public String findNearestIntersectionName(Point2D.Double point) {
        String nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Map.Entry<String, Point2D.Double> entry : intersections.entrySet()) {
            double distance = point.distance(entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = entry.getKey();
            }
        }
        return nearest;
    }

    public String getRandomDestinationName(String excludedIntersection, Random random) {
        List<String> names = new ArrayList<>(intersections.keySet());
        names.remove(excludedIntersection);
        if (names.isEmpty()) {
            return excludedIntersection;
        }
        return names.get(random.nextInt(names.size()));
    }

    public List<Point2D.Double> buildRoute(String startIntersection, String destinationIntersection, boolean reverseLane) {
        if (startIntersection == null || destinationIntersection == null) {
            return Collections.emptyList();
        }
        if (startIntersection.equals(destinationIntersection)) {
            return List.of(copy(intersections.get(startIntersection)));
        }

        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> previous = new HashMap<>();
        queue.add(startIntersection);
        previous.put(startIntersection, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(destinationIntersection)) {
                break;
            }

            for (String next : adjacency.getOrDefault(current, List.of())) {
                if (!previous.containsKey(next)) {
                    previous.put(next, current);
                    queue.add(next);
                }
            }
        }

        if (!previous.containsKey(destinationIntersection)) {
            return Collections.emptyList();
        }

        List<String> path = new ArrayList<>();
        String cursor = destinationIntersection;
        while (cursor != null) {
            path.add(cursor);
            cursor = previous.get(cursor);
        }
        Collections.reverse(path);

        List<Point2D.Double> points = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            Point2D.Double current = intersections.get(path.get(i));
            if (i == path.size() - 1) {
                points.add(copy(current));
                continue;
            }

            Point2D.Double next = intersections.get(path.get(i + 1));
            points.add(applyLaneOffset(current, next, reverseLane));
        }
        return points;
    }

    public void paint(Graphics2D g2) {
        g2.setColor(new Color(70, 74, 82));
        g2.setStroke(new BasicStroke(34f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (RoadSegment segment : roadSegments) {
            Point2D.Double from = intersections.get(segment.from());
            Point2D.Double to = intersections.get(segment.to());
            g2.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
        }

        g2.setColor(new Color(245, 214, 80));
        g2.setStroke(new BasicStroke(2.5f));
        for (RoadSegment segment : roadSegments) {
            Point2D.Double from = intersections.get(segment.from());
            Point2D.Double to = intersections.get(segment.to());
            drawCenterLine(g2, from, to);
        }

        g2.setColor(new Color(230, 230, 230));
        for (Map.Entry<String, Point2D.Double> entry : intersections.entrySet()) {
            Point2D.Double point = entry.getValue();
            g2.drawString(entry.getKey(), (int) point.x - 20, (int) point.y - 12);
        }
    }

    private Point2D.Double applyLaneOffset(Point2D.Double from, Point2D.Double to, boolean reverseLane) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return copy(from);
        }

        double offsetDistance = reverseLane ? -8.0 : 8.0;
        double offsetX = -dy / length * offsetDistance;
        double offsetY = dx / length * offsetDistance;
        return new Point2D.Double(from.x + offsetX, from.y + offsetY);
    }

    private void drawCenterLine(Graphics2D g2, Point2D.Double from, Point2D.Double to) {
        int segments = 12;
        for (int i = 0; i < segments; i += 2) {
            double t1 = (double) i / segments;
            double t2 = (double) (i + 1) / segments;
            int x1 = (int) (from.x + (to.x - from.x) * t1);
            int y1 = (int) (from.y + (to.y - from.y) * t1);
            int x2 = (int) (from.x + (to.x - from.x) * t2);
            int y2 = (int) (from.y + (to.y - from.y) * t2);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void addIntersection(String name, double x, double y) {
        intersections.put(name, new Point2D.Double(x, y));
        adjacency.put(name, new ArrayList<>());
    }

    private void connectTwoWay(String from, String to) {
        adjacency.get(from).add(to);
        adjacency.get(to).add(from);
        roadSegments.add(new RoadSegment(from, to));
    }

    private Point2D.Double copy(Point2D.Double point) {
        return new Point2D.Double(point.x, point.y);
    }

    private record RoadSegment(String from, String to) {
    }
}
