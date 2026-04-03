import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class BounceFrame extends JFrame {
    private final BallCanvas canvas = new BallCanvas();
    private final SimulationStats stats = new SimulationStats();
    private final List<BallWorker> workers = new ArrayList<>();
    private final Random random = new Random();

    private JSpinner speedSpinner;
    private JSpinner sizeSpinner;
    private JSpinner angleSpinner;
    private JSpinner blueBatchSpinner;
    private JTextField txtPocketed;
    private JTextField txtActiveThreads;
    private JTextArea txtLog;

    public BounceFrame() {
        setTitle("Billiard Balls Threads Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(canvas, BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.NORTH);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        log("Application started. Blue balls use MIN priority, red balls use MAX priority.");
    }

    private JPanel buildControlPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.add(buildSettingsPanel(), BorderLayout.NORTH);
        wrapper.add(buildButtonsPanel(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Ball parameters"));

        speedSpinner = new JSpinner(new SpinnerNumberModel(4.0, 1.0, 12.0, 0.5));
        sizeSpinner = new JSpinner(new SpinnerNumberModel(24, 12, 60, 2));
        angleSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 359, 5));
        blueBatchSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 200, 1));

        panel.add(new JLabel("Speed:"));
        panel.add(speedSpinner);
        panel.add(new JLabel("Size:"));
        panel.add(sizeSpinner);
        panel.add(new JLabel("Direction angle:"));
        panel.add(angleSpinner);
        panel.add(new JLabel("Blue balls in experiment:"));
        panel.add(blueBatchSpinner);
        return panel;
    }

    private JPanel buildButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Experiments"));

        JButton btnAddBlue = new JButton("Add Blue Ball");
        JButton btnAddRed = new JButton("Add Red Ball");
        JButton btnPriorityExperiment = new JButton("Priority Experiment");
        JButton btnJoinDemo = new JButton("Join Demo");
        JButton btnStopAll = new JButton("Stop All");

        btnAddBlue.addActionListener(e -> spawnUserBall(BallType.BLUE));
        btnAddRed.addActionListener(e -> spawnUserBall(BallType.RED));
        btnPriorityExperiment.addActionListener(e -> runPriorityExperiment());
        btnJoinDemo.addActionListener(e -> runJoinDemo());
        btnStopAll.addActionListener(e -> stopAllBalls());

        panel.add(btnAddBlue);
        panel.add(btnAddRed);
        panel.add(btnPriorityExperiment);
        panel.add(btnJoinDemo);
        panel.add(btnStopAll);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        txtPocketed = createReadOnlyField("0");
        txtActiveThreads = createReadOnlyField("0");

        statsPanel.add(new JLabel("Pocketed balls:"));
        statsPanel.add(txtPocketed);
        statsPanel.add(Box.createHorizontalStrut(16));
        statsPanel.add(new JLabel("Active threads:"));
        statsPanel.add(txtActiveThreads);

        txtLog = new JTextArea(7, 80);
        txtLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Observations"));

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createReadOnlyField(String value) {
        JTextField textField = new JTextField(value, 6);
        textField.setEditable(false);
        return textField;
    }

    private void spawnUserBall(BallType ballType) {
        int radius = (Integer) sizeSpinner.getValue();
        double speed = (Double) speedSpinner.getValue();
        int angle = (Integer) angleSpinner.getValue();
        startBall(ballType, createEdgeStartPoint(radius), speed, angle, radius);
        log(ballType.getDisplayName() + " ball started with priority " + ballType.getThreadPriority() + ".");
    }

    private void runPriorityExperiment() {
        int blueCount = (Integer) blueBatchSpinner.getValue();
        int radius = (Integer) sizeSpinner.getValue();
        double speed = (Double) speedSpinner.getValue();
        int angle = (Integer) angleSpinner.getValue();
        Point sharedStartPoint = createExperimentStartPoint(radius);
        CountDownLatch startSignal = new CountDownLatch(1);
        List<BallWorker> experimentWorkers = new ArrayList<>();

        for (int i = 0; i < blueCount; i++) {
            experimentWorkers.add(startBall(BallType.BLUE, sharedStartPoint, speed, angle, radius, startSignal));
        }
        experimentWorkers.add(startBall(BallType.RED, sharedStartPoint, speed, angle, radius, startSignal));

        for (BallWorker worker : experimentWorkers) {
            worker.start();
        }
        startSignal.countDown();

        log("Priority experiment started: " + blueCount
                + " blue balls with MIN priority and 1 red ball with MAX priority.");
        log("All balls were released simultaneously from the same point. Now the result is not biased by start order.");
    }

    private void runJoinDemo() {
        int radius = (Integer) sizeSpinner.getValue();
        double speed = (Double) speedSpinner.getValue();
        Point sharedStartPoint = createExperimentStartPoint(radius);

        BallWorker firstWorker = startBall(BallType.BLUE, sharedStartPoint, speed, 20, radius);
        log("Join demo: blue ball started first. Red ball will wait for blue thread to finish.");

        Thread coordinator = new Thread(() -> {
            try {
                firstWorker.join();
                SwingUtilities.invokeLater(() -> {
                    startBall(BallType.RED, sharedStartPoint, speed, 20, radius);
                    log("Join demo result: red ball started only after the blue thread had finished.");
                });
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "join-demo-coordinator");
        coordinator.start();
    }

    private BallWorker startBall(BallType ballType, Point startPoint, double speed, int angleDegrees, int diameter) {
        return startBall(ballType, startPoint, speed, angleDegrees, diameter, null);
    }

    private BallWorker startBall(BallType ballType, Point startPoint, double speed,
                                 int angleDegrees, int diameter, CountDownLatch startSignal) {
        double angleRadians = Math.toRadians(angleDegrees);
        double dx = speed * Math.cos(angleRadians);
        double dy = speed * Math.sin(angleRadians);

        Ball ball = new Ball(startPoint.x, startPoint.y, dx, dy, diameter, ballType.getColor());
        canvas.addBall(ball);

        BallWorker worker = new BallWorker(
                ball,
                canvas,
                this::handleBallPocketed,
                () -> handleWorkerFinished(Thread.currentThread()),
                startSignal
        );
        worker.setPriority(ballType.getThreadPriority());
        worker.setName(ballType.getDisplayName().toLowerCase() + "-ball-" + System.nanoTime());

        synchronized (workers) {
            workers.add(worker);
        }
        refreshActiveThreadCounter();
        if (startSignal == null) {
            worker.start();
        }
        return worker;
    }

    private void handleBallPocketed() {
        SwingUtilities.invokeLater(() -> {
            txtPocketed.setText(String.valueOf(stats.incrementPocketedBalls()));
            log("A ball entered a pocket and disappeared from the table.");
        });
    }

    private void handleWorkerFinished(Thread workerThread) {
        synchronized (workers) {
            workers.removeIf(worker -> worker == workerThread || worker.getState() == Thread.State.TERMINATED);
        }
        SwingUtilities.invokeLater(this::refreshActiveThreadCounter);
    }

    private void stopAllBalls() {
        List<BallWorker> workersSnapshot;
        synchronized (workers) {
            workersSnapshot = new ArrayList<>(workers);
            workers.clear();
        }

        for (BallWorker worker : workersSnapshot) {
            worker.requestStop();
        }

        canvas.clearBalls();
        stats.reset();
        txtPocketed.setText("0");
        refreshActiveThreadCounter();
        log("All ball threads were stopped, and the table was cleared.");
    }

    private void refreshActiveThreadCounter() {
        synchronized (workers) {
            txtActiveThreads.setText(String.valueOf(workers.size()));
        }
    }

    private Point createEdgeStartPoint(int diameter) {
        BilliardTable table = canvas.getTable();
        int width = Math.max(canvas.getWidth(), table.getPreferredSize().width);
        int height = Math.max(canvas.getHeight(), table.getPreferredSize().height);

        int left = table.getInnerLeft();
        int right = table.getInnerRight(width) - diameter;
        int top = table.getInnerTop();
        int bottom = table.getInnerBottom(height) - diameter;

        if (random.nextBoolean()) {
            return new Point(randomInt(left, right), top);
        }
        return new Point(left, randomInt(top, bottom));
    }

    private Point createExperimentStartPoint(int diameter) {
        BilliardTable table = canvas.getTable();
        int width = Math.max(canvas.getWidth(), table.getPreferredSize().width);
        int height = Math.max(canvas.getHeight(), table.getPreferredSize().height);
        int x = table.getInnerLeft() + 60;
        int y = table.getInnerTop() + (table.getInnerBottom(height) - table.getInnerTop()) / 2 - diameter / 2;
        return new Point(x, y);
    }

    private int randomInt(int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    private void log(String message) {
        txtLog.append(message + "\n");
    }
}
