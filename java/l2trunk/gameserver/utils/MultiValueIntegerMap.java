package l2trunk.gameserver.utils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MultiValueIntegerMap {
    private final Map<Integer, List<Integer>> map;

    public MultiValueIntegerMap() {
        map = new ConcurrentHashMap<>();
    }


    public Collection<List<Integer>> values() {
        return map.values();
    }

    public Set<Entry<Integer, List<Integer>>> entrySet() {
        return map.entrySet();
    }

    private List<Integer> remove(Integer key) {
        return map.remove(key);
    }


    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public Integer removeValue(Integer value) {
        List<Integer> toRemove = new ArrayList<>(1);
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            entry.getValue().remove(value);
            if (entry.getValue().isEmpty())
                toRemove.add(entry.getKey());
        }
        for (Integer key : toRemove)
            remove(key);
        return value;
    }

    public Integer put(Integer key, Integer value) {
        List<Integer> coll = map.get(key);
        if (coll == null) {
            coll = new CopyOnWriteArrayList<>();
            map.put(key, coll);
        }
        coll.add(value);
        return value;
    }

    public boolean containsValue(Integer value) {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet())
            if (entry.getValue().contains(value))
                return true;
        return false;
    }

    public int size(Integer key) {
        List<Integer> coll = map.get(key);
        if (coll == null)
            return 0;
        return coll.size();
    }

    public boolean putAll(Integer key, Collection<? extends Integer> values) {
        if (values == null || values.size() == 0)
            return false;
        boolean result = false;
        List<Integer> coll = map.get(key);
        if (coll == null) {
            coll = new CopyOnWriteArrayList<>(values);
            if (coll.size() > 0) {
                map.put(key, coll);
                result = true;
            }
        } else
            result = coll.addAll(values);
        return result;
    }

    public int totalSize() {
        int total = 0;
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet())
            total += entry.getValue().size();
        return total;
    }
}