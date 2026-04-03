import java.util.*;

public class Main {
    public static void main(String[] args) {
        Car car1 = new Car("Ford", "Focus", "Blue", 55.0, 2013);
        Car car2 = new Car("Toyota", "Prius", "Yellow", 45.0, 2018);
        Car car3 = new Car("Toyota", "Prius", "Yellow", 45.0, 2018);
        Car car4 = new Car("Lotus", "Omega", "Red", 100.0, 1997);

        List<Car> carList = new ArrayList<>(Arrays.asList(car1, car2, car3, car4));

        System.out.println("COLLECTION MANAGER");
        CollectionManager collectionManager = new CollectionManager(carList);

        System.out.println("Average tank capacity: " + collectionManager.calculateAverageTankCapacity() + " L");

        System.out.println("\nUnique cars:");
        collectionManager.getUniqueCars().forEach(System.out::println);

        System.out.println("\nSort by tank capacity:");
        collectionManager.sortByTankCapacityMethodRef();
        collectionManager.printCars();


        System.out.println("\nMAP MANAGER");
        Map<String, Car> carMap = new HashMap<>();
        carMap.put("AA1111AA", car1);
        carMap.put("BB2222BB", car2);
        carMap.put("CC3333CC", new Car("Toyota", "Camry", "Black", 60.0, 2021));
        carMap.put("DD4444DD", car4);

        MapManager mapManager = new MapManager(carMap);

        System.out.println("Total tank capacity: " + mapManager.calculateTotalTankCapacity() + " L");

        System.out.println("\nDelete cars older than 2000:");
        mapManager.removeCarsOlderThan(2000);
        mapManager.printMap();

        System.out.println("\nSequential sort by manufacturer, then by year:");
        List<Car> sortedMultiple = mapManager.getSortedValuesMultipleCriteria();
        sortedMultiple.forEach(System.out::println);
    }
}