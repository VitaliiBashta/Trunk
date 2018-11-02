package l2f.commons.lang.reference;

public interface HardReference<T> {
    T get();

    void clear();
}
