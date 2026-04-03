public class Taxi extends Car {
    private final String taxiId;
    private final String driverName;

    public Taxi(String manufacturer, String model, String color, double tankCapacity,
                int producingYear, String taxiId, String driverName) {
        super(manufacturer, model, color, tankCapacity, producingYear);
        if (taxiId == null || taxiId.isBlank()) {
            throw new IllegalArgumentException("Taxi ID is required.");
        }
        if (driverName == null || driverName.isBlank()) {
            throw new IllegalArgumentException("Driver name is required.");
        }

        this.taxiId = taxiId;
        this.driverName = driverName;
    }

    public String getTaxiId() {
        return taxiId;
    }

    public String getDriverName() {
        return driverName;
    }

    @Override
    public void outputCarInfo() {
        System.out.print("[TAXI " + taxiId + " | Driver: " + driverName + "] ");
        super.outputCarInfo();
    }

    @Override
    public String toString() {
        return "[TAXI " + taxiId + "] " + super.toString() + " - Driver: " + driverName;
    }
}
