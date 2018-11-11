package l2trunk.gameserver.model.reference;

import l2trunk.commons.lang.reference.AbstractHardReference;

public class L2Reference<T> extends AbstractHardReference<T> {
    public L2Reference(T reference) {
        super(reference);
    }
}
