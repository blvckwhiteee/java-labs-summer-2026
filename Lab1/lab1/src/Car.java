public class Car {
    private final String manufacturer;
    private final String model;
    private String color;
    private static final int numberOfWheels = 4;
    private double tankCapacity;

    public Car (String manufacturer, String model, String Color, double tankCapacity) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.color = Color;
        this.tankCapacity = tankCapacity;
    }
    public Car (String manufacturer, String model, double tankCapacity) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.tankCapacity = tankCapacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        if (color != null || color.isEmpty()) {
            this.color = color;
        }
    }

    public double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(double newTankCapacity) {
        if (tankCapacity > 0)  {
            tankCapacity = newTankCapacity;
        }
    }

    public static int getNumberOfWheels() {
        return numberOfWheels;
    }

    public void outputCarInfo(double maxSpeed, int producingYear) {
        System.out.println("\nCar Info");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        if (color != null) System.out.println("Color: " + color);
        System.out.println("Max speed: " + maxSpeed);
        System.out.println("Produced in: " + producingYear);
    }

    public void outputCarInfo(int producingYear) {
        System.out.println("\nCar Info");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        if (color != null) System.out.println("Color: " + color);
        System.out.println("Produced in: " + producingYear);
    }


}
