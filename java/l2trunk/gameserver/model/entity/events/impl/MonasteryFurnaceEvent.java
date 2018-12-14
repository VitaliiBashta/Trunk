package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public final class MonasteryFurnaceEvent extends GlobalEvent {
    public static final String FURNACE_ROOM = "furnace_room";
    public static final String PROTECTOR_ROOM = "Protector_Room";
    public static final String FIGHTER_ROOM = "Fighter_Room";
    public static final String MYSTIC_ROOM = "Mystic_Room";
    public static final String STANDART_ROOM = "Standart_Monster";
    private long startTime;
    private boolean progress;

    public MonasteryFurnaceEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void startEvent() {
        progress = true;
        super.startEvent();
    }

    @Override
    public void stopEvent() {
        progress = false;
        super.stopEvent();
    }

    @Override
    public boolean isInProgress() {
        return progress;
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
        startTime = System.currentTimeMillis();
        registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return startTime;
    }
}