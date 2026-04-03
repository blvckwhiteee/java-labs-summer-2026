import java.util.List;

public class GenericUtils {

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

    public static void printListElements(List<?> list) {
        System.out.println("--- Друк елементів списку (розмір: " + list.size() + ") ---");
        for (Object item : list) {
            System.out.println(" • " + item.toString());
        }
    }
}