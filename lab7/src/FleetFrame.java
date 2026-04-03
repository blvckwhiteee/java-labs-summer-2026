import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FleetFrame extends JFrame {
    private final List<Taxi> taxiList = new ArrayList<>();
    private final TaxiObjectStorage taxiObjectStorage = new TaxiObjectStorage();
    private final TaxiParametersStorage taxiParametersStorage = new TaxiParametersStorage();

    private JTextField txtManufacturer;
    private JTextField txtModel;
    private JTextField txtColor;
    private JTextField txtTank;
    private JTextField txtYear;
    private JTextField txtTaxiId;
    private JTextField txtDriver;
    private JTextArea txtOutput;
    private JLabel lblClock;
    private JLabel lblAnimationStatus;
    private FleetAnimationPanel animationPanel;
    private Timer clockTimer;
    private Timer animationTimer;

    public FleetFrame() {
        setTitle("Taxi Fleet Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setJMenuBar(buildMenuBar());

        add(buildInputPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        configureTimers();
        restoreFleetOnStartup();
        displayFleet();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveObjectsItem = new JMenuItem("Save Objects");
        JMenuItem loadObjectsItem = new JMenuItem("Load Objects");
        JMenuItem saveParametersItem = new JMenuItem("Save Parameters");
        JMenuItem loadParametersItem = new JMenuItem("Load Parameters");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveObjectsItem.addActionListener(e -> saveObjectsToDefaultFile());
        loadObjectsItem.addActionListener(e -> loadObjectsFromDefaultFile());
        saveParametersItem.addActionListener(e -> saveParametersToDefaultFile());
        loadParametersItem.addActionListener(e -> loadParametersFromDefaultFile());
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(saveObjectsItem);
        fileMenu.add(loadObjectsItem);
        fileMenu.addSeparator();
        fileMenu.add(saveParametersItem);
        fileMenu.add(loadParametersItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    private JPanel buildInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 6, 6));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Taxi data"));

        inputPanel.add(new JLabel("Manufacturer:"));
        txtManufacturer = new JTextField();
        inputPanel.add(txtManufacturer);

        inputPanel.add(new JLabel("Model:"));
        txtModel = new JTextField();
        inputPanel.add(txtModel);

        inputPanel.add(new JLabel("Color:"));
        txtColor = new JTextField();
        inputPanel.add(txtColor);

        inputPanel.add(new JLabel("Tank (liters):"));
        txtTank = new JTextField();
        inputPanel.add(txtTank);

        inputPanel.add(new JLabel("Producing Year:"));
        txtYear = new JTextField();
        inputPanel.add(txtYear);

        inputPanel.add(new JLabel("Taxi ID:"));
        txtTaxiId = new JTextField();
        inputPanel.add(txtTaxiId);

        inputPanel.add(new JLabel("Driver:"));
        txtDriver = new JTextField();
        inputPanel.add(txtDriver);

        JButton btnAdd = new JButton("Add Taxi");
        btnAdd.addActionListener(e -> addTaxiAction());
        inputPanel.add(btnAdd);

        JButton btnSelectInfo = new JButton("Selected Taxi");
        btnSelectInfo.addActionListener(e -> showSelectedTaxiInfo());
        inputPanel.add(btnSelectInfo);

        return inputPanel;
    }

    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        animationPanel = new FleetAnimationPanel();
        JScrollPane visualWrapper = new JScrollPane(animationPanel);
        visualWrapper.setBorder(BorderFactory.createTitledBorder("Fleet visualization"));
        centerPanel.add(visualWrapper);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JScrollPane outputPane = new JScrollPane(txtOutput);
        outputPane.setBorder(BorderFactory.createTitledBorder("Results and logs"));
        centerPanel.add(outputPane);

        return centerPanel;
    }

    private JPanel buildControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnShowFleet = new JButton("Show Fleet");
        JButton btnTotalCapacity = new JButton("Total Capacity");
        JButton btnFindNewest = new JButton("Find Newest");
        JButton btnStart = new JButton("Start");
        JButton btnPause = new JButton("Pause");
        JButton btnStop = new JButton("Stop");

        btnShowFleet.addActionListener(e -> displayFleet());
        btnTotalCapacity.addActionListener(e -> calculateTotalCapacity());
        btnFindNewest.addActionListener(e -> findNewestTaxi());
        btnStart.addActionListener(e -> startAnimation());
        btnPause.addActionListener(e -> pauseAnimation());
        btnStop.addActionListener(e -> stopAnimation());

        lblAnimationStatus = new JLabel("Animation: stopped");
        lblAnimationStatus.setForeground(new Color(170, 60, 60));

        lblClock = new JLabel("Current Time: --:--:--");
        lblClock.setForeground(new Color(45, 87, 195));

        controlPanel.add(btnShowFleet);
        controlPanel.add(btnTotalCapacity);
        controlPanel.add(btnFindNewest);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(btnStart);
        controlPanel.add(btnPause);
        controlPanel.add(btnStop);
        controlPanel.add(Box.createHorizontalStrut(16));
        controlPanel.add(lblAnimationStatus);
        controlPanel.add(Box.createHorizontalStrut(16));
        controlPanel.add(lblClock);

        return controlPanel;
    }

    private void configureTimers() {
        clockTimer = new Timer(1000, this::updateTime);
        clockTimer.start();

        animationTimer = new Timer(40, e -> animationPanel.animateStep());
    }

    private void seedDemoData() {
        addTaxiToFleet(new Taxi("Toyota", "Prius", "White", 45.0, 2018, "TX-101", "Misha"));
        addTaxiToFleet(new Taxi("Skoda", "Octavia", "Green", 60.0, 2021, "TX-202", "Maxim"));
        addTaxiToFleet(new Taxi("Nissan", "Leaf", "Yellow", 40.0, 2020, "TX-303", "Anna"));
    }

    private void restoreFleetOnStartup() {
        try {
            Path defaultObjectsFile = StoragePaths.getObjectsFile();
            if (Files.exists(defaultObjectsFile)) {
                replaceFleet(taxiObjectStorage.load(defaultObjectsFile));
                txtOutput.append(">> Fleet restored from serialized objects file.\n");
                return;
            }
        } catch (IOException | ClassNotFoundException ex) {
            txtOutput.append(">> Could not restore serialized objects on startup: " + ex.getMessage() + "\n");
        }

        seedDemoData();
        txtOutput.append(">> Demo fleet loaded. No saved objects file found.\n");
    }

    private void addTaxiAction() {
        try {
            Taxi newTaxi = buildTaxiFromForm();
            addTaxiToFleet(newTaxi);
            txtOutput.append(">> Successfully added: " + newTaxi + "\n");
            clearInputFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tank and producing year must be numeric values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Taxi buildTaxiFromForm() {
        String manufacturer = txtManufacturer.getText().trim();
        String model = txtModel.getText().trim();
        String color = txtColor.getText().trim();
        String taxiId = txtTaxiId.getText().trim();
        String driverName = txtDriver.getText().trim();

        double tank = Double.parseDouble(txtTank.getText().trim());
        int year = Integer.parseInt(txtYear.getText().trim());

        ensureUniqueTaxiId(taxiId);
        return new Taxi(manufacturer, model, color, tank, year, taxiId, driverName);
    }

    private void ensureUniqueTaxiId(String taxiId) {
        for (Taxi taxi : taxiList) {
            if (taxi.getTaxiId().equalsIgnoreCase(taxiId)) {
                throw new IllegalArgumentException("Taxi ID must be unique.");
            }
        }
    }

    private void addTaxiToFleet(Taxi taxi) {
        ensureUniqueTaxiId(taxi.getTaxiId());
        taxiList.add(taxi);
        animationPanel.addTaxi(taxi);
    }

    private void replaceFleet(List<Taxi> taxis) {
        taxiList.clear();
        taxiList.addAll(taxis);
        animationPanel.setTaxis(taxiList);
    }

    private void clearInputFields() {
        txtManufacturer.setText("");
        txtModel.setText("");
        txtColor.setText("");
        txtTank.setText("");
        txtYear.setText("");
        txtTaxiId.setText("");
        txtDriver.setText("");
    }

    private void displayFleet() {
        txtOutput.append("Current Fleet:\n");
        if (taxiList.isEmpty()) {
            txtOutput.append("Fleet is empty.\n");
            return;
        }

        for (Taxi taxi : taxiList) {
            txtOutput.append(taxi + " (Tank: " + taxi.getTankCapacity() + " liters)\n");
        }
        txtOutput.append("--------------------------\n");
    }

    private void calculateTotalCapacity() {
        if (taxiList.isEmpty()) {
            txtOutput.append(">> Fleet is empty. Total capacity is 0.\n");
            return;
        }

        Fleet<Taxi> fleet = new Fleet<>("City taxi fleet", taxiList.toArray(new Taxi[0]));
        Fleet<Taxi>.FleetStatistics statistics = fleet.new FleetStatistics();
        txtOutput.append(">> Total tanks capacity: "
                + statistics.calculateTotalFleetTanksCapacity() + " liters.\n");
    }

    private void findNewestTaxi() {
        if (taxiList.isEmpty()) {
            txtOutput.append(">> Fleet is empty.\n");
            return;
        }

        Taxi newest = GenericUtils.findMaxElement(taxiList.toArray(new Taxi[0]));
        if (newest != null) {
            txtOutput.append(">> Newest taxi: " + newest + "\n");
        }
    }

    private void showSelectedTaxiInfo() {
        Taxi selectedTaxi = animationPanel.getSelectedTaxi();
        if (selectedTaxi == null) {
            txtOutput.append(">> No taxi selected on the visualization panel.\n");
            return;
        }

        txtOutput.append(">> Selected taxi: " + selectedTaxi + ", tank: "
                + selectedTaxi.getTankCapacity() + " liters.\n");
    }

    private void startAnimation() {
        if (taxiList.isEmpty()) {
            txtOutput.append(">> Add at least one taxi before starting animation.\n");
            return;
        }

        animationTimer.start();
        lblAnimationStatus.setText("Animation: running");
        lblAnimationStatus.setForeground(new Color(25, 120, 35));
        txtOutput.append(">> Animation started. Drag taxis to set initial positions before stop/reset.\n");
    }

    private void pauseAnimation() {
        animationTimer.stop();
        lblAnimationStatus.setText("Animation: paused");
        lblAnimationStatus.setForeground(new Color(180, 120, 30));
        txtOutput.append(">> Animation paused.\n");
    }

    private void stopAnimation() {
        animationTimer.stop();
        animationPanel.resetAnimation();
        lblAnimationStatus.setText("Animation: stopped");
        lblAnimationStatus.setForeground(new Color(170, 60, 60));
        txtOutput.append(">> Animation stopped and positions restored.\n");
    }

    private void updateTime(ActionEvent event) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        lblClock.setText("Current Time: " + sdf.format(new Date()));
    }

    private void saveObjectsToDefaultFile() {
        try {
            Path objectsFile = StoragePaths.getObjectsFile();
            taxiObjectStorage.save(taxiList, objectsFile);
            txtOutput.append(">> Objects saved to: " + objectsFile.toAbsolutePath() + "\n");
        } catch (IOException ex) {
            showStorageError("Could not save serialized objects.", ex);
        }
    }

    private void loadObjectsFromDefaultFile() {
        try {
            Path objectsFile = StoragePaths.getObjectsFile();
            replaceFleet(taxiObjectStorage.load(objectsFile));
            txtOutput.append(">> Objects restored from: " + objectsFile.toAbsolutePath() + "\n");
            displayFleet();
        } catch (IOException | ClassNotFoundException ex) {
            showStorageError("Could not load serialized objects.", ex);
        }
    }

    private void saveParametersToDefaultFile() {
        try {
            Path parametersFile = StoragePaths.getParametersFile();
            taxiParametersStorage.save(taxiList, parametersFile);
            txtOutput.append(">> Parameters saved to: " + parametersFile.toAbsolutePath() + "\n");
        } catch (IOException ex) {
            showStorageError("Could not save parameters file.", ex);
        }
    }

    private void loadParametersFromDefaultFile() {
        try {
            Path parametersFile = StoragePaths.getParametersFile();
            replaceFleet(taxiParametersStorage.load(parametersFile));
            txtOutput.append(">> Fleet restored from parameters file: "
                    + parametersFile.toAbsolutePath() + "\n");
            displayFleet();
        } catch (IOException ex) {
            showStorageError("Could not load parameters file.", ex);
        }
    }

    private void showStorageError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + "\n" + ex.getMessage(),
                "Storage Error", JOptionPane.ERROR_MESSAGE);
        txtOutput.append(">> " + message + " " + ex.getMessage() + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FleetFrame().setVisible(true));
    }
}
