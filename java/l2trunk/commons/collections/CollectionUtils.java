package l2trunk.commons.collections;

import java.util.Collection;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <E> int hashCode(Collection<E> collection) {
        int hashCode = 1;
        for (E obj : collection) {
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public static <E> E safeGet(List<E> list, int index) {
        return list.size() > index ? list.get(index) : null;
    }
}
