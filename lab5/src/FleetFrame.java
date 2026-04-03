import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FleetFrame extends JFrame {
    private List<Taxi> taxiList = new ArrayList<>();

    private JTextField txtManufacturer, txtModel, txtColor, txtTank, txtYear, txtTaxiId, txtDriver;
    private JTextArea txtOutput;
    private JLabel lblClock;

    public FleetFrame() {
        setTitle("Taxi Fleet Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder());

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
        inputPanel.add(btnAdd);

        add(inputPanel, BorderLayout.NORTH);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results and logs"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton btnShowFleet = new JButton("Show Fleet");
        JButton btnTotalCapacity = new JButton("Total Capacity");
        JButton btnFindNewest = new JButton("Find Newest");

        lblClock = new JLabel("Current Time: --:--:--");
        lblClock.setForeground(Color.BLUE);

        controlPanel.add(btnShowFleet);
        controlPanel.add(btnTotalCapacity);
        controlPanel.add(btnFindNewest);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(lblClock);

        add(controlPanel, BorderLayout.SOUTH);



        btnAdd.addActionListener(e -> addTaxiAction());
        btnShowFleet.addActionListener(e -> displayFleet());
        btnTotalCapacity.addActionListener(e -> calculateTotalCapacity());
        btnFindNewest.addActionListener(e -> findNewestTaxi());


        Timer timer = new Timer(1000, this::updateTime);
        timer.start();

        taxiList.add(new Taxi("Toyota", "Prius", "White", 45.0, 2018, "TX-101", "Misha"));
        taxiList.add(new Taxi("Skoda", "Octavia", "Green", 60.0, 2021, "TX-202", "Maxim"));
        displayFleet();
    }

    private void addTaxiAction() {
        try {

            String manuf = txtManufacturer.getText().trim();
            String model = txtModel.getText().trim();
            String color = txtColor.getText().trim();
            String id = txtTaxiId.getText().trim();
            String driver = txtDriver.getText().trim();

            if (manuf.isEmpty() || model.isEmpty() || id.isEmpty() || driver.isEmpty()) {
                throw new IllegalArgumentException("Enter all fields");
            }

            double tank = Double.parseDouble(txtTank.getText().trim());
            int year = Integer.parseInt(txtYear.getText().trim());

            if (tank <= 0 || year < 1886 || year > 2026) {
                throw new IllegalArgumentException("Invalid capacity or producing year values!");
            }

            Taxi newTaxi = new Taxi(manuf, model, color, tank, year, id, driver);
            taxiList.add(newTaxi);

            txtOutput.append(">> Successfully added: " + newTaxi.toString() + "\n");

            txtManufacturer.setText(""); txtModel.setText(""); txtColor.setText("");
            txtTank.setText(""); txtYear.setText(""); txtTaxiId.setText(""); txtDriver.setText("");

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this, "Tank and producing year must be a numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayFleet() {
        txtOutput.append("Current Fleet:\n");
        if (taxiList.isEmpty()) {
            txtOutput.append("Fleet is empty.\n");
            return;
        }
        for (Taxi t : taxiList) {
            txtOutput.append(t.toString() + " (Tank: " + t.getTankCapacity() + " liters)\n");
        }
        txtOutput.append("--------------------------\n");
    }

    private void calculateTotalCapacity() {
        if (taxiList.isEmpty()) return;

        Taxi[] array = taxiList.toArray(new Taxi[0]);
        Fleet<Taxi> fleet = new Fleet<>("Calculating tanks capacity", array);

        double total = 0;
        for (Taxi t : fleet.getVehicles()) {
            total += t.getTankCapacity();
        }
        txtOutput.append(">> Total tanks capacity: " + total + " liters.\n");
    }

    private void findNewestTaxi() {
        if (taxiList.isEmpty()) return;
        Taxi[] array = taxiList.toArray(new Taxi[0]);
        Taxi newest = GenericUtils.findMaxElement(array);
        if (newest != null) {
            txtOutput.append(">> Newest taxi: " + newest.toString() + "\n");
        }
    }

    private void updateTime(ActionEvent e) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        lblClock.setText("Current Time: " + sdf.format(new Date()));
    }

    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
            new FleetFrame().setVisible(true);
        });
    }
}