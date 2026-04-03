public class Route {
    private double distanceKm;

    public Route(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double calculateTripCost(Taxi taxi, double pricePerKm) {
        System.out.println("Розрахунок маршруту для водія " + taxi.getDriverName() + "...");
        return distanceKm * pricePerKm;
    }
}