import java.util.Arrays;

public class Fleet<T extends Car> {
    private final String fleetName;
    private final T[] vehicles;

    public Fleet(String fleetName, T[] vehicles) {
        if (fleetName == null || fleetName.isBlank()) {
            throw new IllegalArgumentException("Fleet name is required.");
        }
        if (vehicles == null) {
            throw new IllegalArgumentException("Vehicles array is required.");
        }

        this.fleetName = fleetName;
        this.vehicles = vehicles;
    }

    public String getFleetName() {
        return fleetName;
    }

    public T[] getVehicles() {
        return vehicles;
    }

    public class FleetStatistics {
        public double calculateTotalFleetTanksCapacity() {
            double total = 0;
            for (T vehicle : vehicles) {
                total += vehicle.getTankCapacity();
            }
            return total;
        }

        public void displayTotalFleetTanksCapacity() {
            System.out.println("Total tank capacity of fleet '" + fleetName + "': "
                    + calculateTotalFleetTanksCapacity() + " L.");
        }
    }

    public void performFleetAudit() {
        class Auditor {
            void check() {
                System.out.println("Audit completed. Vehicles count: " + vehicles.length);
            }
        }

        Auditor auditor = new Auditor();
        auditor.check();
    }

    public T findCarByModel(String targetModel) {
        if (targetModel == null || targetModel.isBlank()) {
            return null;
        }

        for (T vehicle : vehicles) {
            if (vehicle.getModel().equalsIgnoreCase(targetModel)) {
                return vehicle;
            }
        }
        return null;
    }

    public void sortVehiclesByYear() {
        Arrays.sort(vehicles);
    }

    public T[] getFleetBackup() {
        return Arrays.copyOf(vehicles, vehicles.length);
    }
}
