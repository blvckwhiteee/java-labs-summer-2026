import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class ConwayGameOfLifePanel extends JPanel {
    private static final int CELL_SIZE = 18;
    private static final Color GRID_COLOR = new Color(210, 214, 220);
    private static final Color DEAD_CELL_COLOR = new Color(248, 249, 251);
    private static final Color LIVE_CELL_COLOR = new Color(46, 122, 214);

    private final boolean[][] grid;
    private int generation;
    private boolean editingEnabled = true;

    public ConwayGameOfLifePanel(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive.");
        }

        grid = new boolean[rows][columns];
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (!editingEnabled) {
                    return;
                }

                int column = event.getX() / CELL_SIZE;
                int row = event.getY() / CELL_SIZE;
                toggleCell(row, column);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(grid[0].length * CELL_SIZE, grid.length * CELL_SIZE);
    }

    public int getGeneration() {
        return generation;
    }

    public void setEditingEnabled(boolean editingEnabled) {
        this.editingEnabled = editingEnabled;
    }

    public void clearGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                grid[row][column] = false;
            }
        }
        generation = 0;
        repaint();
    }

    public void randomize(double aliveProbability) {
        if (aliveProbability < 0 || aliveProbability > 1) {
            throw new IllegalArgumentException("Alive probability must be between 0 and 1.");
        }

        Random random = new Random();
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                grid[row][column] = random.nextDouble() < aliveProbability;
            }
        }
        generation = 0;
        repaint();
    }

    public void nextGeneration() {
        boolean[][] nextGrid = new boolean[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                int liveNeighbours = countLiveNeighbours(row, column);
                nextGrid[row][column] = shouldCellLive(row, column, liveNeighbours);
            }
        }

        for (int row = 0; row < grid.length; row++) {
            System.arraycopy(nextGrid[row], 0, grid[row], 0, grid[row].length);
        }

        generation++;
        repaint();
    }

    private void toggleCell(int row, int column) {
        if (row < 0 || row >= grid.length || column < 0 || column >= grid[row].length) {
            return;
        }

        grid[row][column] = !grid[row][column];
        repaint();
    }

    private int countLiveNeighbours(int row, int column) {
        int liveNeighbours = 0;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                int neighbourRow = row + rowOffset;
                int neighbourColumn = column + columnOffset;

                if (neighbourRow >= 0 && neighbourRow < grid.length
                        && neighbourColumn >= 0 && neighbourColumn < grid[neighbourRow].length
                        && grid[neighbourRow][neighbourColumn]) {
                    liveNeighbours++;
                }
            }
        }

        return liveNeighbours;
    }

    private boolean shouldCellLive(int row, int column, int liveNeighbours) {
        if (grid[row][column]) {
            return liveNeighbours == 2 || liveNeighbours == 3;
        }
        return liveNeighbours == 3;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                g2.setColor(grid[row][column] ? LIVE_CELL_COLOR : DEAD_CELL_COLOR);
                g2.fillRect(column * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g2.setColor(GRID_COLOR);
                g2.drawRect(column * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        g2.dispose();
    }
}
