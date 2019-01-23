package l2trunk.commons.lang.reference;

public class HardReferences {

    private HardReferences() {
    }

    @SuppressWarnings("unchecked")
    public static <T> HardReference<T> emptyRef() {
        return (HardReference<T>) new AbstractHardReference(null);
    }

}
