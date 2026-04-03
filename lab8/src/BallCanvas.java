import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class BallCanvas extends JPanel {
    private final List<Ball> balls = new ArrayList<>();
    private final BilliardTable table = new BilliardTable();

    public BallCanvas() {
        setPreferredSize(table.getPreferredSize());
    }

    public synchronized void addBall(Ball ball) {
        balls.add(ball);
        repaint();
    }

    public synchronized void removeBall(Ball ball) {
        balls.remove(ball);
        repaint();
    }

    public synchronized void clearBalls() {
        balls.clear();
        repaint();
    }

    public synchronized List<Ball> getBallsSnapshot() {
        return new ArrayList<>(balls);
    }

    public BilliardTable getTable() {
        return table;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        table.paint(g2, getWidth(), getHeight());
        for (Ball ball : getBallsSnapshot()) {
            ball.draw(g2);
        }
        g2.dispose();
    }
}
