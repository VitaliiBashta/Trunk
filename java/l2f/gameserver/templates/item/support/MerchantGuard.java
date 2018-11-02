package l2f.gameserver.templates.item.support;

import l2f.gameserver.model.entity.SevenSigns;

import java.util.Set;

public class MerchantGuard {
    private int _itemId;
    private int _npcId;
    private int _max;
    private Set<Integer> _ssq;

    public MerchantGuard(int itemId, int npcId, int max, Set<Integer> ssq) {
        _itemId = itemId;
        _npcId = npcId;
        _max = max;
        _ssq = ssq;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getNpcId() {
        return _npcId;
    }

    public int getMax() {
        return _max;
    }

    public boolean isValidSSQPeriod() {
        return SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION && _ssq.contains(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE));
    }
}
