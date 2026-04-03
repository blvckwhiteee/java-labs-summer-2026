public class SimulationStats {
    private int pocketedBallsCount;

    public synchronized void reset() {
        pocketedBallsCount = 0;
    }

    public synchronized int incrementPocketedBalls() {
        pocketedBallsCount++;
        return pocketedBallsCount;
    }

    public synchronized int getPocketedBallsCount() {
        return pocketedBallsCount;
    }
}
