public class Taxi extends Car {
    private final String taxiId;
    private final String driverName;

    public Taxi(String manufacturer, String model, String color, double tankCapacity,
                int producingYear, String taxiId, String driverName) {
        super(manufacturer, model, color, tankCapacity, producingYear);
        this.taxiId = taxiId;
        this.driverName = driverName;
    }

    public String getDriverName() { return driverName; }

    @Override
    public void outputCarInfo() {
        System.out.print("[TAXI " + taxiId + " | Driver: " + driverName + "] ");
        super.outputCarInfo();
    }
}