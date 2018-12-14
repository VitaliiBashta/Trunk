package l2trunk.commons.lang;

public class Pair<K, V> {
    K first;
    V second;

    public Pair(K first, V second) {
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
