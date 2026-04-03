import java.util.Arrays;

public class Fleet<T extends Car> {
    private String fleetName;
    private T[] vehicles;

    public Fleet(String fleetName, T[] vehicles) {
        this.fleetName = fleetName;
        this.vehicles = vehicles;
    }

    public T[] getVehicles() { return vehicles; }

    public class FleetStatistics {
        public void displayTotalFleetTanksCapacity() {
            double total = 0;
            for (T vehicle : vehicles) {
                total += vehicle.getTankCapacity();
            }
            System.out.println("Загальна місткість баків автопарку '" + fleetName + "': " + total + " л.");
        }
    }

    public void performFleetAudit() {
        class Auditor {
            void check() {
                System.out.println("Аудит пройдено. Кількість ТЗ: " + vehicles.length);
            }
        }
        Auditor auditor = new Auditor();
        auditor.check();
    }

    public T findCarByModel(String targetModel) {
        for (T vehicle : vehicles) {
            if (vehicle.getModel().equalsIgnoreCase(targetModel)) {
                return vehicle;
            }
        }
        return null;
    }

    public void sortVehiclesByYear() {
        Arrays.sort(vehicles);
        System.out.println("Автопарк '" + fleetName + "' відсортовано за роком випуску.");
    }

    public T[] getFleetBackup() {
        return Arrays.copyOf(vehicles, vehicles.length);
    }
}