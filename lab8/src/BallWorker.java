import java.util.concurrent.CountDownLatch;

public class BallWorker extends Thread {
    private static final int FRAME_DELAY_MS = 8;

    private final Ball ball;
    private final BallCanvas canvas;
    private final Runnable pocketedCallback;
    private final Runnable finishedCallback;
    private final CountDownLatch startSignal;
    private volatile boolean running = true;

    public BallWorker(Ball ball, BallCanvas canvas, Runnable pocketedCallback,
                      Runnable finishedCallback, CountDownLatch startSignal) {
        this.ball = ball;
        this.canvas = canvas;
        this.pocketedCallback = pocketedCallback;
        this.finishedCallback = finishedCallback;
        this.startSignal = startSignal;
    }

    @Override
    public void run() {
        try {
            if (startSignal != null) {
                startSignal.await();
            }

            while (running && !ball.isPocketed()) {
                boolean reachedPocket = ball.move(canvas.getTable(), canvas.getWidth(), canvas.getHeight());
                if (reachedPocket) {
                    canvas.removeBall(ball);
                    pocketedCallback.run();
                    break;
                }

                canvas.repaint();
                Thread.sleep(FRAME_DELAY_MS);
            }
        } catch (InterruptedException ignored) {
            interrupt();
        } finally {
            finishedCallback.run();
        }
    }

    public void requestStop() {
        running = false;
        interrupt();
    }
}
