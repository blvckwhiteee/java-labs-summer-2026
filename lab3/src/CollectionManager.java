import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private Collection<Car> cars;

    public CollectionManager(Collection<Car> cars) {
        this.cars = cars;
    }

    public Car findCarByModel(String model) {
        return cars.stream()
                .filter(car -> car.getModel().equalsIgnoreCase(model))
                .findFirst()
                .orElse(null);
    }

    public Set<Car> getUniqueCars() {
        return new HashSet<>(cars);
    }

    public List<Car> filterCarsNewerThan(int year) {
        return cars.stream()
                .filter(car -> car.getProducingYear() > year)
                .collect(Collectors.toList());
    }

    public double calculateAverageTankCapacity() {
        return cars.stream()
                .mapToDouble(Car::getTankCapacity)
                .average()
                .orElse(0.0);
    }

    public void sortByTankCapacityAnonymous() {
        List<Car> list = new ArrayList<>(cars);
        list.sort(new Comparator<Car>() {
            @Override
            public int compare(Car c1, Car c2) {
                return Double.compare(c1.getTankCapacity(), c2.getTankCapacity());
            }
        });
        this.cars = list;
    }

    public void sortByTankCapacityLambda() {
        List<Car> list = new ArrayList<>(cars);
        list.sort((c1, c2) -> Double.compare(c1.getTankCapacity(), c2.getTankCapacity()));
        this.cars = list;
    }

    public void sortByTankCapacityMethodRef() {
        List<Car> list = new ArrayList<>(cars);
        Collections.sort(list);
        this.cars = list;
    }

    public void printCars() {
        cars.forEach(System.out::println);
    }
}