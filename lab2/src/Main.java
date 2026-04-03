public class Main {
    public static void main(String[] args) {
        Car[] myCars = {
                new Car("Ford", "Focus", "Blue", 55.0, 2013),
                new Taxi("Toyota", "Prius", "White", 45.0, 2018, "TX-101", "Misha Zhmih"),
                new Car("Lotus", "Omega", "Black", 100.0, 1997),
                new Taxi("Skoda", "Octavia", "Green", 60.0, 2021, "TX-202", "Maxim Not Zhmih")
        };

        TaxiFleet myTaxiFleet = new TaxiFleet("Шайтан Експрес", myCars);

        for (Car car : myTaxiFleet.getVehicles()) {
            car.outputCarInfo();
        }

        myTaxiFleet.performFleetAudit();

        TaxiFleet.FleetStatistics stats = myTaxiFleet.new FleetStatistics();
        stats.displayTotalFleetTanksCapacity();



        Route tripToCenter = new Route(15.5);
        if (myCars[1] instanceof Taxi) {
            double cost = tripToCenter.calculateTripCost((Taxi) myCars[1], 12.0);
            System.out.println("Trip cost: " + cost);
        }

        Car maxTankCar = myTaxiFleet.findCarWithMaxTankCapacity();
        System.out.println("Car with max capacity: " + maxTankCar.getManufacturer() + " (" + maxTankCar.getTankCapacity() + "L)");

        Car foundCar = myTaxiFleet.findCarByModel("Prius");
        System.out.println("Found 'Prius': " + (foundCar != null));

        myTaxiFleet.sortVehiclesByYear();
        for (Car car : myTaxiFleet.getVehicles()) {
            System.out.println(car.getManufacturer() + " - " + car.getProducingYear());
        }


        Car[] backup = myTaxiFleet.getFleetBackup();
        System.out.println("Backup created with size: " + backup.length);
    }
}