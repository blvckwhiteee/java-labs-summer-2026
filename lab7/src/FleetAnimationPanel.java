import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FleetAnimationPanel extends JPanel {
    private static final int PREF_WIDTH = 900;
    private static final int PREF_HEIGHT = 420;

    private final List<TaxiVisualState> taxiStates = new ArrayList<>();
    private final Random random = new Random();
    private final RoadNetwork roadNetwork = new RoadNetwork();

    private TaxiVisualState selectedTaxi;
    private Point dragOffset;
    private boolean dragging;

    public FleetAnimationPanel() {
        setBackground(new Color(235, 242, 224));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                selectTaxiAt(event.getPoint());
                dragging = false;
                if (selectedTaxi != null) {
                    dragOffset = new Point(
                            (int) (event.getX() - selectedTaxi.getBounds().getX()),
                            (int) (event.getY() - selectedTaxi.getBounds().getY())
                    );
                }
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                TaxiVisualState taxiState = findTaxiAt(event.getPoint());
                if (taxiState != null && event.getClickCount() >= 2) {
                    assignRandomRoute(taxiState);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (selectedTaxi != null && dragging) {
                    selectedTaxi.storeCurrentPositionAsInitial();
                    assignRandomRoute(selectedTaxi);
                }
                dragOffset = null;
                dragging = false;
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (selectedTaxi == null || dragOffset == null) {
                    return;
                }

                dragging = true;
                double newX = event.getX() - dragOffset.x;
                double newY = event.getY() - dragOffset.y;
                selectedTaxi.setPosition(
                        clamp(newX, 10, getWidth() - selectedTaxi.getWidth() - 10),
                        clamp(newY, 10, getHeight() - selectedTaxi.getHeight() - 10)
                );
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                setCursor(findTaxiAt(event.getPoint()) == null
                        ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public void addTaxi(Taxi taxi) {
        java.awt.geom.Point2D.Double spawnPoint = roadNetwork.getRandomSpawnPoint(random);
        TaxiVisualState taxiState = new TaxiVisualState(taxi, spawnPoint.x, spawnPoint.y);
        assignRandomRoute(taxiState);
        taxiState.storeCurrentPositionAsInitial();
        taxiStates.add(taxiState);
        repaint();
    }

    public void setTaxis(List<Taxi> taxis) {
        taxiStates.clear();
        for (Taxi taxi : taxis) {
            addTaxi(taxi);
        }
        clearSelection();
        repaint();
    }

    public void animateStep() {
        for (TaxiVisualState taxiState : taxiStates) {
            boolean reachedTarget = taxiState.updatePosition();
            if (reachedTarget) {
                assignRandomRoute(taxiState);
            }
        }
        repaint();
    }

    public void resetAnimation() {
        for (TaxiVisualState taxiState : taxiStates) {
            taxiState.resetToInitialPosition();
        }
        repaint();
    }

    public Taxi getSelectedTaxi() {
        return selectedTaxi == null ? null : selectedTaxi.getTaxi();
    }

    private void selectTaxiAt(Point point) {
        TaxiVisualState newSelection = findTaxiAt(point);
        if (selectedTaxi != null) {
            selectedTaxi.setSelected(false);
        }
        selectedTaxi = newSelection;
        if (selectedTaxi != null) {
            selectedTaxi.setSelected(true);
        }
    }

    private TaxiVisualState findTaxiAt(Point point) {
        for (int i = taxiStates.size() - 1; i >= 0; i--) {
            TaxiVisualState taxiState = taxiStates.get(i);
            if (taxiState.contains(point.getX(), point.getY())) {
                return taxiState;
            }
        }
        return null;
    }

    private void clearSelection() {
        if (selectedTaxi != null) {
            selectedTaxi.setSelected(false);
        }
        selectedTaxi = null;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        roadNetwork.paint(g2);
        paintLegend(g2);
        for (TaxiVisualState taxiState : taxiStates) {
            paintTaxi(g2, taxiState);
        }

        g2.dispose();
    }

    private void paintLegend(Graphics2D g2) {
        g2.setColor(new Color(25, 25, 25));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Taxi fleet animation area", 20, 24);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Drag a taxi to set its initial position. Double-click a taxi to assign a new route.", 20, 44);
    }

    private void paintTaxi(Graphics2D g2, TaxiVisualState taxiState) {
        int x = (int) taxiState.getBounds().getX();
        int y = (int) taxiState.getBounds().getY();
        int width = taxiState.getWidth();
        int height = taxiState.getHeight();

        g2.setColor(taxiState.getBodyColor());
        g2.fillRoundRect(x, y, width, height, 12, 12);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x + 12, y + 7, width - 24, 10);
        g2.fillOval(x + 8, y + height - 6, 12, 12);
        g2.fillOval(x + width - 20, y + height - 6, 12, 12);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, width, height, 12, 12);

        if (taxiState.isSelected()) {
            g2.setColor(new Color(65, 125, 255));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x - 3, y - 3, width + 6, height + 6, 14, 14);
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString(taxiState.getLabel(), x, y - 6);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString("Route to " + taxiState.getDestinationName(), x, y + height + 14);
    }

    private void assignRandomRoute(TaxiVisualState taxiState) {
        String startIntersection = roadNetwork.findNearestIntersectionName(taxiState.getCurrentPosition());
        String destinationIntersection = roadNetwork.getRandomDestinationName(startIntersection, random);
        applyRoute(taxiState, startIntersection, destinationIntersection);
    }

    private void applyRoute(TaxiVisualState taxiState,
                            String startIntersection,
                            String destinationIntersection) {
        taxiState.setRoute(
                roadNetwork.buildRoute(startIntersection, destinationIntersection, false),
                destinationIntersection
        );
        taxiState.setRouteMetadata(startIntersection, destinationIntersection, false);
    }
}
