import java.awt.Color;

public enum BallType {
    BLUE("Blue", new Color(45, 115, 245), Thread.MIN_PRIORITY),
    RED("Red", new Color(214, 52, 52), Thread.MAX_PRIORITY);

    private final String displayName;
    private final Color color;
    private final int threadPriority;

    BallType(String displayName, Color color, int threadPriority) {
        this.displayName = displayName;
        this.color = color;
        this.threadPriority = threadPriority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public int getThreadPriority() {
        return threadPriority;
    }
}
