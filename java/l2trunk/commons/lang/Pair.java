package l2trunk.commons.lang;

public class Pair<K, V> {
    private K first;
    private V second;
    public static <K,V> Pair<K,V> of (K key, V value) {
        return new Pair<>(key,value);
    }

    private Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public V getValue() {
        return second;
    }

    public K getKey() {
        return first;
    }
}
