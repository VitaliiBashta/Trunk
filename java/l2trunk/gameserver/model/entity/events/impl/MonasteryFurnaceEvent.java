package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public class MonasteryFurnaceEvent extends GlobalEvent {
    public static final String FURNACE_ROOM = "furnace_room";
    public static final String PROTECTOR_ROOM = "Protector_Room";
    public static final String FIGHTER_ROOM = "Fighter_Room";
    public static final String MYSTIC_ROOM = "Mystic_Room";
    public static final String STANDART_ROOM = "Standart_Monster";
    private long _startTime;
    private boolean _progress;

    public MonasteryFurnaceEvent(MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void startEvent() {
        _progress = true;
        super.startEvent();
    }

    @Override
    public void stopEvent() {
        _progress = false;
        super.stopEvent();
    }

    @Override
    public boolean isInProgress() {
        return _progress;
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
        _startTime = System.currentTimeMillis();
        registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return _startTime;
    }
}