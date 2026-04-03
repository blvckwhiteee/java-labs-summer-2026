import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Car[] mixedCars = {
                new Car("Ford", "Focus", "Blue", 55.0, 2013),
                new Taxi("Toyota", "Prius", "White", 45.0, 2018, "TX-101", "Misha Zhmih"),
                new Car("Lotus", "Omega", "Black", 100.0, 1997)
        };

        Taxi[] pureTaxis = {
                new Taxi("Skoda", "Octavia", "Green", 60.0, 2021, "TX-202", "Maxim Not Zhmih"),
                new Taxi("Renault", "Logan", "Yellow", 50.0, 2019, "TX-303", "Ivan")
        };

        Fleet<Car> generalFleet = new Fleet<>("General Fleet", mixedCars);
        Fleet<Taxi> taxiFleet = new Fleet<>("Шайтан Експрес", pureTaxis);

        generalFleet.performFleetAudit();
        Fleet<Taxi>.FleetStatistics taxiStats = taxiFleet.new FleetStatistics();
        taxiStats.displayTotalFleetTanksCapacity();

        Car newestMixedCar = GenericUtils.findMaxElement(mixedCars);
        System.out.println("Newest car in mixed fleet: " + newestMixedCar.getManufacturer() + " (" + newestMixedCar.getProducingYear() + ")");

        Taxi newestTaxi = GenericUtils.findMaxElement(pureTaxis);
        System.out.println("Newest Taxi: " + newestTaxi.getManufacturer() + " (" + newestTaxi.getProducingYear() + ")");

        Integer[] numbers = {10, 42, 7, 99, 1};
        Integer maxNumber = GenericUtils.findMaxElement(numbers);
        System.out.println("Max number: " + maxNumber);

        List<Car> carList = new ArrayList<>();
        carList.add(mixedCars[0]);
        carList.add(mixedCars[1]);

        List<String> stringList = new ArrayList<>();
        stringList.add("Route 1: Kyiv - Lviv");
        stringList.add("Route 2: Odesa - Kharkiv");

        GenericUtils.printListElements(carList);
        GenericUtils.printListElements(stringList);
    }
}