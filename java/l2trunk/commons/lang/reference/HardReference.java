package l2trunk.commons.lang.reference;

public interface HardReference<T> {
    T get();

    void clear();
}
