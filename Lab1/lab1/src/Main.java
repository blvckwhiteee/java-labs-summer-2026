public class Main {
    public static void main() {
        System.out.println("Number of wheels: " + Car.getNumberOfWheels()); // static метод, що використовує static поле

        // ініціалізація 2-ох об'єктів із різними конструкторами
        Car blueCar = new Car("Ford", "Focus", "Blue", 80.5);
        Car someCar = new Car("Lotus", "Omega", 100);
        Car[] cars = {blueCar, someCar};

        System.out.println("Car color: " + blueCar.getColor());

        CarWithoutConstructor carWithoutConstructor = new CarWithoutConstructor();
        carWithoutConstructor.makeSound();
        // Фактично створити клас без явного конструктора можливо,
        // але при компіляції автоматично створюється конструктор без аргументів


        // Явна демонстрація overloaded-методів
        blueCar.outputCarInfo(2013);
        someCar.outputCarInfo(240.3, 1997);
        someCar.setColor("Red");

        System.out.println("To fill all tanks you need pay:" + calculateTotalPriceToFillAllCars(cars));

    }

    public static double calculateTotalPriceToFillAllCars(Car [] cars) {
        double totalPrice = 0;
        double pricePerLiter = 60.2;
        for (Car car : cars) {
            totalPrice += car.getTankCapacity() * pricePerLiter;
        }
        return totalPrice;
    }
}
