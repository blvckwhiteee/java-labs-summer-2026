import java.util.Objects;

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

    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public double getTankCapacity() { return tankCapacity; }
    public int getProducingYear() { return producingYear; }

    @Override
    public int compareTo(Car other) {
        return Double.compare(this.tankCapacity, other.tankCapacity);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d), Color: %s, Tank: %.1fL",
                manufacturer, model, producingYear, color, tankCapacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Double.compare(car.tankCapacity, tankCapacity) == 0 &&
                producingYear == car.producingYear &&
                Objects.equals(manufacturer, car.manufacturer) &&
                Objects.equals(model, car.model) &&
                Objects.equals(color, car.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manufacturer, model, color, tankCapacity, producingYear);
    }
}