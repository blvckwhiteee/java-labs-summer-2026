public class Car implements Comparable<Car> {
    private final String manufacturer;
    private final String model;
    private String color;
    private double tankCapacity;
    private int producingYear;

    public Car(String manufacturer, String model, String color, double tankCapacity, int producingYear) {
        if (manufacturer == null || manufacturer.isBlank()) {
            throw new IllegalArgumentException("Manufacturer is required.");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model is required.");
        }
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Color is required.");
        }
        if (tankCapacity <= 0) {
            throw new IllegalArgumentException("Tank capacity must be positive.");
        }
        if (producingYear < 1886 || producingYear > 2026) {
            throw new IllegalArgumentException("Producing year is out of supported range.");
        }

        this.manufacturer = manufacturer;
        this.model = model;
        this.color = color;
        this.tankCapacity = tankCapacity;
        this.producingYear = producingYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        if (color != null && !color.isBlank()) {
            this.color = color;
        }
    }

    public double getTankCapacity() {
        return tankCapacity;
    }

    public int getProducingYear() {
        return producingYear;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void outputCarInfo() {
        System.out.println("Car Info: " + manufacturer + " " + model + ", Color: " + color
                + ", Year: " + producingYear + ", Tank: " + tankCapacity + "L");
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
