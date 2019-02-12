package l2trunk.commons.lang;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.mapregion.RegionData;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public static boolean equalLists(List<Skill> one, List<Skill> two) {
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
    public static  RegionData[] add(RegionData[] array, RegionData element) {
        Class type = array != null ? array.getClass().getComponentType() :  element.getClass();
        RegionData[] newArray = copyArrayGrow(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static RegionData[] copyArrayGrow(RegionData[] array, Class<? extends RegionData> type) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            RegionData[] newArray = (RegionData[]) Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return (RegionData[]) Array.newInstance(type, 1);
    }

    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i])
                return i;

        return -1;
    }
}
