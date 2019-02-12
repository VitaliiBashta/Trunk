package l2trunk.gameserver.model.entity.events;

import l2trunk.gameserver.taskmanager.actionrunner.ActionWrapper;

public final class EventWrapper extends ActionWrapper {
    private final GlobalEvent event;
    private final int time;

    EventWrapper(String name, GlobalEvent event, int time) {
        super(name);
        this.event = event;
        this.time = time;
    }

    @Override
    public void runImpl0() {
        event.timeActions(time);
    }
}
