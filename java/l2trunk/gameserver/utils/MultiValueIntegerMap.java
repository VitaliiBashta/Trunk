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

    private void remove(Integer key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public void removeValue(Integer value) {
        List<Integer> toRemove = new ArrayList<>(1);
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            entry.getValue().remove(value);
            if (entry.getValue().isEmpty())
                toRemove.add(entry.getKey());
        }
        toRemove.forEach(this::remove);
    }

    public void put(Integer key, Integer value) {
        List<Integer> coll = map.get(key);
        if (coll == null) {
            coll = new CopyOnWriteArrayList<>();
            map.put(key, coll);
        }
        coll.add(value);
    }

    public boolean containsValue(Integer value) {
        return map.values().stream()
                .anyMatch(entry -> entry.contains(value));
    }

    public void putAll(Integer key, Collection<Integer> values) {
        if (values == null || values.size() == 0)
            return;
        List<Integer> coll = map.get(key);
        if (coll == null) {
            coll = new CopyOnWriteArrayList<>(values);
            if (coll.size() > 0) {
                map.put(key, coll);
            }
        } else
            coll.addAll(values);
    }

    public int totalSize() {
        return map.values().stream()
                .mapToInt(List::size)
                .sum();
    }
}