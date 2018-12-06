package l2trunk.commons.lang;

import java.lang.reflect.Array;
import java.util.*;

public final class ArrayUtils {
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final int INDEX_NOT_FOUND = -1;

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

    @Deprecated
    public static boolean contains(int[] array, int value) {
        if (array == null)
            return false;

        for (int item : array)
            if (value == item)
                return true;
        return false;
    }


    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i])
                return i;

        return INDEX_NOT_FOUND;
    }

    public static int indexOf(Integer[] array, int value) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i])
                return i;

        return INDEX_NOT_FOUND;
    }


    private static <T> void eqBrute(T[] a, int lo, int hi, Comparator<T> c) {
        if ((hi - lo) == 1) {
            if (c.compare(a[hi], a[lo]) < 0) {
                T e = a[lo];
                a[lo] = a[hi];
                a[hi] = e;
            }
        } else if ((hi - lo) == 2) {
            int pmin = c.compare(a[lo], a[lo + 1]) < 0 ? lo : lo + 1;
            pmin = c.compare(a[pmin], a[lo + 2]) < 0 ? pmin : lo + 2;
            if (pmin != lo) {
                T e = a[lo];
                a[lo] = a[pmin];
                a[pmin] = e;
            }
            eqBrute(a, lo + 1, hi, c);
        } else if ((hi - lo) == 3) {
            int pmin = c.compare(a[lo], a[lo + 1]) < 0 ? lo : lo + 1;
            pmin = c.compare(a[pmin], a[lo + 2]) < 0 ? pmin : lo + 2;
            pmin = c.compare(a[pmin], a[lo + 3]) < 0 ? pmin : lo + 3;
            if (pmin != lo) {
                T e = a[lo];
                a[lo] = a[pmin];
                a[pmin] = e;
            }
            int pmax = c.compare(a[hi], a[hi - 1]) > 0 ? hi : hi - 1;
            pmax = c.compare(a[pmax], a[hi - 2]) > 0 ? pmax : hi - 2;
            if (pmax != hi) {
                T e = a[hi];
                a[hi] = a[pmax];
                a[pmax] = e;
            }
            eqBrute(a, lo + 1, hi - 1, c);
        }
    }

    private static <T> void eqSort(T[] a, int lo0, int hi0, Comparator<T> c) {
        int lo = lo0;
        int hi = hi0;
        if ((hi - lo) <= 3) {
            eqBrute(a, lo, hi, c);
            return;
        } /* * Pick a pivot and move it out of the way */
        T pivot = a[(lo + hi) / 2];
        a[(lo + hi) / 2] = a[hi];
        a[hi] = pivot;
        while (lo < hi) { /* * Search forward from a[lo] until an element is found that * is greater than the pivot or lo >= hi */
            while (c.compare(a[lo], pivot) <= 0 && lo < hi) {
                lo++;
            } /* * Search backward from a[hi] until element is found that * is less than the pivot, or hi <= lo */
            while (c.compare(pivot, a[hi]) <= 0 && lo < hi) {
                hi--;
            } /* * Swap elements a[lo] and a[hi] */
            if (lo < hi) {
                T e = a[lo];
                a[lo] = a[hi];
                a[hi] = e;
            }
        } /* * Put the median in the "center" of the list */
        a[hi0] = a[hi];
        a[hi] = pivot; /* * Recursive calls, elements a[lo0] to a[lo-1] are less than or * equal to pivot, elements a[hi+1] to a[hi0] are greater than * pivot. */
        eqSort(a, lo0, lo - 1, c);
        eqSort(a, hi + 1, hi0, c);
    }

    /**
     * An enhanced quick sort
     *
     * @author Jim Boritz
     */
    public static <T> void eqSort(T[] a, Comparator<T> c) {
        eqSort(a, 0, a.length - 1, c);
    }

    public static int[] toArray(Collection<Integer> collection) {
        int[] ar = new int[collection.size()];
        int i = 0;
        for (Integer t : collection)
            ar[i++] = t;
        return ar;
    }

    public static List<Integer> createAscendingList(int min, int max) {
        List<Integer> list = new ArrayList<>(max - min + 1);
        int j = min;
        while (j <= max)
            list.add(j++);
        return list;
    }
}
