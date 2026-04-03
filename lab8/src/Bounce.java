import javax.swing.SwingUtilities;

public class Bounce {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BounceFrame().setVisible(true));
    }
}
