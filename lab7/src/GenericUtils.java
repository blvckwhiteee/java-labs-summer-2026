import java.util.List;

public final class GenericUtils {
    private GenericUtils() {
    }

    public static <T extends Comparable<? super T>> T findMaxElement(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        T max = array[0];
        for (T element : array) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }

    public static void printListElements(List<?> list) {
        if (list == null) {
            System.out.println("List is null.");
            return;
        }

        System.out.println("--- Printing list elements (size: " + list.size() + ") ---");
        for (Object item : list) {
            System.out.println(" - " + item);
        }
    }
}
