package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public class FantasiIsleParadEvent extends GlobalEvent {
    public FantasiIsleParadEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
        clearActions();
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis() + 30000L;
    }
}