import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class ConwayGameOfLifeFrame extends JFrame {
    private static final int DEFAULT_ROWS = 25;
    private static final int DEFAULT_COLUMNS = 35;

    private final ConwayGameOfLifePanel gamePanel;
    private final Timer timer;
    private final JLabel generationLabel;
    private final JLabel statusLabel;
    private final JSpinner densitySpinner;
    private final JSlider speedSlider;

    public ConwayGameOfLifeFrame() {
        setTitle("Conway Game of Life");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        gamePanel = new ConwayGameOfLifePanel(DEFAULT_ROWS, DEFAULT_COLUMNS);
        generationLabel = new JLabel("Generation: 0");
        statusLabel = new JLabel("Status: editing");
        densitySpinner = new JSpinner(new SpinnerNumberModel(30, 5, 95, 5));
        speedSlider = new JSlider(50, 500, 150);
        timer = new Timer(speedSlider.getValue(), e -> advanceSimulation());

        add(buildTopPanel(), BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        speedSlider.addChangeListener(e -> timer.setDelay(speedSlider.getValue()));

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Model controls"));

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Next step");
        JButton clearButton = new JButton("Clear");
        JButton randomButton = new JButton("Random state");

        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        stepButton.addActionListener(e -> advanceSimulation());
        clearButton.addActionListener(e -> clearSimulation());
        randomButton.addActionListener(e -> randomizeSimulation());

        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(stepButton);
        panel.add(clearButton);
        panel.add(randomButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("Alive %:"));
        panel.add(densitySpinner);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("Speed (ms):"));
        panel.add(speedSlider);

        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        panel.add(generationLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(statusLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Click cells to define the initial state before start."));
        return panel;
    }

    private void startSimulation() {
        gamePanel.setEditingEnabled(false);
        timer.start();
        statusLabel.setText("Status: running");
    }

    private void pauseSimulation() {
        timer.stop();
        gamePanel.setEditingEnabled(true);
        statusLabel.setText("Status: paused");
    }

    private void clearSimulation() {
        timer.stop();
        gamePanel.setEditingEnabled(true);
        gamePanel.clearGrid();
        refreshGenerationLabel();
        statusLabel.setText("Status: cleared");
    }

    private void randomizeSimulation() {
        timer.stop();
        gamePanel.setEditingEnabled(true);
        int densityPercent = (Integer) densitySpinner.getValue();
        gamePanel.randomize(densityPercent / 100.0);
        refreshGenerationLabel();
        statusLabel.setText("Status: random initial state created");
    }

    private void advanceSimulation() {
        gamePanel.nextGeneration();
        refreshGenerationLabel();
    }

    private void refreshGenerationLabel() {
        generationLabel.setText("Generation: " + gamePanel.getGeneration());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConwayGameOfLifeFrame().setVisible(true));
    }
}
