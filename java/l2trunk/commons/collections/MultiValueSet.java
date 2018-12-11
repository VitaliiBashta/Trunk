package l2trunk.commons.collections;

import l2trunk.commons.lang.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.toInt;

public class MultiValueSet<T> extends HashMap<T, Object> {
    public MultiValueSet() {
        super();
    }

    public MultiValueSet(int size) {
        super(size);
    }

    public MultiValueSet(MultiValueSet<T> set) {
        super(set);
    }

    public void set(T key, Object value) {
        put(key, value);
    }

    public void set(T key, String value) {
        put(key, value);
    }

    public void set(T key, boolean value) {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public void set(T key, int value) {
        put(key, value);
    }

    public void set(T key, int[] value) {
        put(key, value);
    }

    public void set(T key, long value) {
        put(key, value);
    }

    public void set(T key, double value) {
        put(key, value);
    }

    public void set(T key, Enum<?> value) {
        put(key, value);
    }

    public void unset(T key) {
        remove(key);
    }

    public boolean isSet(T key) {
        return get(key) != null;
    }

    @Override
    public MultiValueSet<T> clone() {
        return new MultiValueSet<>(this);
    }

    public boolean getBool(T key) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).intValue() != 0;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        if (val instanceof Boolean)
            return (Boolean) val;

        throw new IllegalArgumentException("Boolean value required, but found: " + val + "!");
    }

    public boolean getBool(T key, boolean defaultValue) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).intValue() != 0;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        if (val instanceof Boolean)
            return (Boolean) val;

        return defaultValue;
    }

    public int getInteger(T key) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).intValue();
        if (val instanceof String)
            return Integer.parseInt((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1 : 0;

        throw new IllegalArgumentException("Integer value required, but found: " + val + "!");
    }

    public int getInteger(T key, int defaultValue) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).intValue();
        if (val instanceof String)
            return Integer.parseInt((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1 : 0;

        return defaultValue;
    }

    public List<Integer> getIntegerList(T key) {
        Object val = get(key);

        if (val instanceof List<?>)
            return (List<Integer>) val;
        if (val instanceof Number)
            return Collections.singletonList((Integer)val);
        if (val instanceof String) {
            List<String> vals = Arrays.asList(((String) val).split(";"));
                return vals.stream()
                        .map(NumberUtils::toInt)
                        .collect(Collectors.toList());

        }
        throw new IllegalArgumentException("Integer list required, but found: " + val + "!");
    }

    public List<Integer> getIntegerList(T key, List<Integer> defaultArray) {
        try {
            return getIntegerList(key);
        } catch (IllegalArgumentException e) {
            return defaultArray;
        }
    }

    public long getLong(T key) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).longValue();
        if (val instanceof String)
            return Long.parseLong((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1L : 0L;

        throw new IllegalArgumentException("Long value required, but found: " + val + "!");
    }

    public long getLong(T key, long defaultValue) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).longValue();
        if (val instanceof String)
            return Long.parseLong((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1L : 0L;

        return defaultValue;
    }

    public float getFloat(String key) {
        Object val = get(key);
        if (val == null) {
            throw new IllegalArgumentException("Float value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).floatValue();
        }
        try {
            return (float) Double.parseDouble((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Float value required, but found: " + val);
        }
    }

    public float getFloat(final String key, final float defaultValue) {
        final Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).floatValue();
        if (val instanceof String)
            return Float.parseFloat((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1 : 0;

        return defaultValue;
    }

    public double getDouble(T key) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).doubleValue();
        if (val instanceof String)
            return Double.parseDouble((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1. : 0.;

        throw new IllegalArgumentException("Double value required, but found: " + val + "!");
    }

    public double getDouble(T key, double defaultValue) {
        Object val = get(key);

        if (val instanceof Number)
            return ((Number) val).doubleValue();
        if (val instanceof String)
            return Double.parseDouble((String) val);
        if (val instanceof Boolean)
            return (Boolean) val ? 1. : 0.;

        return defaultValue;
    }

    public String getString(T key) {
        Object val = get(key);

        if (val != null)
            return String.valueOf(val);

        throw new IllegalArgumentException("String value required, but not specified!");
    }

    public String getString(T key, String defaultValue) {
        Object val = get(key);

        if (val != null)
            return String.valueOf(val);

        return defaultValue;
    }

    public Object getObject(T key) {
        return get(key);
    }

    public Object getObject(T key, Object defaultValue) {
        Object val = get(key);

        if (val != null)
            return val;

        return defaultValue;
    }

    public short getShort(String key) {
        Object val = get(key);
        if (val == null) {
            throw new IllegalArgumentException("Short value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }
        try {
            return Short.parseShort((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Short value required, but found: " + val);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getEnum(T name, Class<E> enumClass) {
        Object val = get(name);

        if (enumClass.isInstance(val))
            return (E) val;
        if (val instanceof String)
            return Enum.valueOf(enumClass, (String) val);

        throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + "required, but found: " + val + "!");
    }

    public <E extends Enum<E>> E getEnum(T name, Class<E> enumClass, E defaultValue) {
        Object val = get(name);

        if (enumClass.isInstance(val))
            return (E) val;
        if (val instanceof String)
            return Enum.valueOf(enumClass, (String) val);

        return defaultValue;
    }
}
