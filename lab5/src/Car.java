public class Car implements Comparable<Car> {
    private final String manufacturer;
    private final String model;
    private String color;
    private double tankCapacity;
    private int producingYear;

    public Car(String manufacturer, String model, String color, double tankCapacity, int producingYear) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.color = color;
        this.tankCapacity = tankCapacity;
        this.producingYear = producingYear;
    }

    public String getColor() { return color; }
    public void setColor(String color) {
        if (color != null && !color.isEmpty()) {
            this.color = color;
        }
    }

    public double getTankCapacity() { return tankCapacity; }
    public int getProducingYear() { return producingYear; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }

    public void outputCarInfo() {
        System.out.println("Car Info: " + manufacturer + " " + model + ", Color: " + color +
                ", Year: " + producingYear + ", Tank: " + tankCapacity + "L");
    }

    @Override
    public String toString() {
        return manufacturer + " " + model + " (" + producingYear + ")";
    }

    @Override
    public int compareTo(Car other) {
        return Integer.compare(this.producingYear, other.producingYear);
    }
}