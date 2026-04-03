import java.util.Collections;
import java.util.List;

public class GenericUtils {

    // Завдання 2
    public static <T extends Comparable<? super T>> T findMaxElement(T[] array) {
        if (array == null || array.length == 0) return null;

        T max = array[0];
        for (T element : array) {
            if (element.compareTo(max) > 0) {
                max = element;
            }
        }
        return max;
    }

    // Завдання 4
    public static void printListElements(List<?> list) {
        System.out.println("List elements (size: " + list.size() + ")");
        for (Object item : list) {
            System.out.println(item.toString());
        }
    }

    // Завдання 5
    // Collections.copy()
}