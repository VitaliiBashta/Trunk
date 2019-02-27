package l2trunk.commons.collections;

import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.*;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.*;

public class StatsSet {
    public static final StatsSet EMPTY = new StatsSet();
    private Map<String, Object> map = new HashMap<>();

    public StatsSet() {
    }

    public StatsSet(StatsSet set) {
        map = new HashMap<>(set.map);
    }

    public StatsSet set(String key, List<Location> value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, Location value) {
        map.put(key, value);
        return this;
    }
    public StatsSet set(String key, StatsSet value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, Polygon value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, Territory value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, Residence value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, StatTemplate value) {
        map.put(key, value);
        return this;
    }

    public StatsSet set(String key, String value) {
        map.put(key, value);
        return this;
    }

    public void set(String key, boolean value) {
        map.put(key, value);
    }

    public StatsSet set(String key, int value) {
        map.put(key, value);
        return this;
    }

    public void set(String key, int[] value) {
        map.put(key, value);
    }

    public void set(String key, long value) {
        map.put(key, value);
    }

    public StatsSet set(String key, double value) {
        map.put(key, value);
        return this;
    }

    public void set(String key, Enum<?> value) {
        map.put(key, value);
    }

    public void unset(String key) {
        map.remove(key);
    }

    public boolean isSet(String key) {
        return map.containsKey(key);
    }

    public boolean getBool(String key) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).intValue() != 0;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        if (val instanceof Boolean)
            return (Boolean) val;

        throw new IllegalArgumentException("Boolean value required, but found: " + val + "!");
    }

    public boolean getBool(String key, boolean defaultValue) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).intValue() != 0;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        if (val instanceof Boolean)
            return (boolean) val;

        return defaultValue;
    }

    public int getInteger(String key) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).intValue();
        if (val instanceof String)
            return toInt((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1 : 0;
return 0;
//        throw new IllegalArgumentException("Integer value required, but found: " + val + "!");
    }


    public int getInteger(String key, int defaultValue) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).intValue();
        if (val instanceof String)
            return toInt((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1 : 0;

        return defaultValue;
    }

    public List<Integer> getIntegerList(String key) {
        Object val = map.get(key);

        if (val instanceof List<?>)
            return (List<Integer>) val;
        if (val instanceof Number)
            return List.of((Integer) val);
        if (val instanceof String) {
            List<String> vals = Arrays.asList(((String) val).split(";"));
            return vals.stream()
                    .map(NumberUtils::toInt)
                    .collect(Collectors.toList());

        }
        throw new IllegalArgumentException("Integer list required, but found: " + val + "!");
    }

    public List<Integer> getIntegerList(String key, List<Integer> defaultArray) {
        try {
            return getIntegerList(key);
        } catch (IllegalArgumentException e) {
            return defaultArray;
        }
    }

    public long getLong(String key) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).longValue();
        if (val instanceof String)
            return toLong((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1L : 0L;
        return 0;
//        throw new IllegalArgumentException("Long value required, but found: " + val + "!");
    }

    public long getLong(String key, long defaultValue) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).longValue();
        if (val instanceof String)
            return toLong((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1L : 0L;

        return defaultValue;
    }

    public double getDouble(String key) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).doubleValue();
        if (val instanceof String)
            return toDouble((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1. : 0.;

        throw new IllegalArgumentException("Double value required, but found: " + val + "!");
    }

    public double getDouble(String key, double defaultValue) {
        Object val = map.get(key);

        if (val instanceof Number)
            return ((Number) val).doubleValue();
        if (val instanceof String)
            return toDouble((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1. : 0.;

        return defaultValue;
    }

    public String getString(String key) {
        Object val = map.get(key);

        if (val != null)
            return val.toString();

        throw new IllegalArgumentException("String value required, but not specified!");
    }

    public String getString(String key, String defaultValue) {
        Object val = map.get(key);

        if (val != null)
            return val.toString();

        return defaultValue;
    }

    public Object getObject(String key) {
        return map.get(key);
    }

    public <E extends Enum<E>> E getEnum(String name, Class<E> enumClass) {
        Object val = map.get(name);

        if (enumClass.isInstance(val))
            return (E) val;
        if (val instanceof String)
            return Enum.valueOf(enumClass, (String) val);

        throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + "required, but found: " + val + "!");
    }

    public <E extends Enum<E>> E getEnum(String name, Class<E> enumClass, E defaultValue) {
        Object val = map.get(name);

        if (enumClass.isInstance(val))
            return (E) val;
        if (val instanceof String)
            return Enum.valueOf(enumClass, (String) val);

        return defaultValue;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public StatsSet getStats(String key) {
        if (map.get(key) instanceof StatsSet)
            return (StatsSet) map.get(key);
        return StatsSet.EMPTY;
    }

    public Location getLocation(String key) {
        if (map.get(key) instanceof Location)
            return (Location) map.get(key);
        return Location.of();
    }

    public Territory getTerritory(String key) {
        if (map.get(key) instanceof Territory)
            return (Territory) map.get(key);
        return new Territory();
    }

    public Residence getResidence(String key) {
        if (map.get(key) instanceof Residence)
            return (Residence) map.get(key);
        return null;
    }

    public Polygon getPolygon(String key) {
        if (map.get(key) instanceof Polygon)
            return (Polygon) map.get(key);
        return null;
    }

    public List<Location> getLocations(String key) {
        if (map.get(key) instanceof List<?>) {
            return (List<Location>) map.get(key);
        }
        return List.of();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
