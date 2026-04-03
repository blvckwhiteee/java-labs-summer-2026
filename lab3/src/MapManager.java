import java.util.*;
import java.util.stream.Collectors;

public class MapManager {
    private Map<String, Car> carMap;

    public MapManager(Map<String, Car> carMap) {
        this.carMap = carMap;
    }

    public Map<String, Car> filterByManufacturer(String manufacturer) {
        return carMap.entrySet().stream()
                .filter(entry -> entry.getValue().getManufacturer().equalsIgnoreCase(manufacturer))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void removeCarsOlderThan(int year) {
        carMap.values().removeIf(car -> car.getProducingYear() < year);
    }

    public double calculateTotalTankCapacity() {
        return carMap.values().stream()
                .mapToDouble(Car::getTankCapacity)
                .sum();
    }

    public List<Car> getSortedValuesMultipleCriteria() {
        List<Car> list = new ArrayList<>(carMap.values());

        Comparator<Car> anonymousChain = new Comparator<Car>() {
            @Override
            public int compare(Car c1, Car c2) {
                int manCompare = c1.getManufacturer().compareTo(c2.getManufacturer());
                if (manCompare == 0) {
                    return Integer.compare(c1.getProducingYear(), c2.getProducingYear());
                }
                return manCompare;
            }
        };

        Comparator<Car> lambdaChain = (c1, c2) -> {
            int manCompare = c1.getManufacturer().compareTo(c2.getManufacturer());
            return (manCompare != 0) ? manCompare : Integer.compare(c1.getProducingYear(), c2.getProducingYear());
        };

        Comparator<Car> methodRefChain = Comparator.comparing(Car::getManufacturer)
                .thenComparingInt(Car::getProducingYear);

        list.sort(methodRefChain);
        return list;
    }

    public void printMap() {
        carMap.forEach((key, car) -> System.out.println("Key: " + key + " -> " + car));
    }
}