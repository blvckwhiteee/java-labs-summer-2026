import java.util.Arrays;

public class TaxiFleet {
    private String fleetName;
    private Car[] vehicles;

    public TaxiFleet(String fleetName, Car[] vehicles) {
        this.fleetName = fleetName;
        this.vehicles = vehicles;
    }

    public Car[] getVehicles() { return vehicles; }

    // Inner class
    public class FleetStatistics {
        public void displayTotalFleetTanksCapacity() {
            double total = 0;
            for (Car car : vehicles) {
                total += car.getTankCapacity();
            }
            System.out.println("Total fuel capacity of '" + fleetName + "': " + total + " Liters.");
        }
    }

    public void performFleetAudit() {
        // Local class
        class Auditor {
            void check() {
                System.out.println("Total vehicles audited: " + vehicles.length);
            }
        }

        Auditor auditor = new Auditor();
        auditor.check();
    }

    public Car findCarWithMaxTankCapacity() {
        if (vehicles.length == 0) return null;
        Car maxCar = vehicles[0];
        for (Car car : vehicles) {
            if (car.getTankCapacity() > maxCar.getTankCapacity()) {
                maxCar = car;
            }
        }
        return maxCar;
    }

    public Car findCarByModel(String targetModel) {
        for (Car car : vehicles) {
            if (car.getModel().equalsIgnoreCase(targetModel)) {
                return car;
            }
        }
        return null;
    }

    public void sortVehiclesByYear() {
        Arrays.sort(vehicles);
        System.out.println("Sorted by producing year.");
    }

    public Car[] getFleetBackup() {
        return Arrays.copyOf(vehicles, vehicles.length);
    }
}