package l2trunk.gameserver.templates.item.support;

import l2trunk.gameserver.model.entity.SevenSigns;

import java.util.Set;

public final class MerchantGuard {
    private final int itemId;
    private final int npcId;
    private final int max;
    private final Set<Integer> ssq;

    public MerchantGuard(int itemId, int npcId, int max, Set<Integer> ssq) {
        this.itemId = itemId;
        this.npcId = npcId;
        this.max = max;
        this.ssq = ssq;
    }

    public int getItemId() {
        return itemId;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getMax() {
        return max;
    }

    public boolean isValidSSQPeriod() {
        return SevenSigns.INSTANCE.getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION && ssq.contains(SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE));
    }
}
