package l2trunk.commons.lang;

import java.lang.reflect.Array;
import java.util.*;

public final class ArrayUtils {
    /**
     * Check if index is in valid range of array, if so return array value
     *
     * @return array element or null, if index out of range
     */
    public static <T> T valid(T[] array, int index) {
        if (array == null)
            return null;
        if (index < 0 || array.length <= index)
            return null;
        return array[index];
    }

    public static <T extends Comparable<T>> boolean equalLists(List<T> one, List<T> two) {
        if (one == null && two == null) {
            return true;
        }

        if (one == null || two == null || one.size() != two.size()) {
            return false;
        }


        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    /**
     * Enlarge and add element to array
     *
     * @return new array with element
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T[] add(T[] array, T element) {
        Class type = array != null ? array.getClass().getComponentType() : element != null ? element.getClass() : Object.class;
        T[] newArray = (T[]) copyArrayGrow(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] copyArrayGrow(T[] array, Class<? extends T> type) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return (T[]) Array.newInstance(type, 1);
    }

    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i])
                return i;

        return -1;
    }

    public static List<Integer> createAscendingList(int min, int max) {
        List<Integer> list = new ArrayList<>(max - min + 1);
        int j = min;
        while (j <= max)
            list.add(j++);
        return list;
    }
}
