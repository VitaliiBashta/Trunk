package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public class StartStopAction implements EventAction {
    public static final String EVENT = "event";

    private final String name;
    private final boolean start;

    public StartStopAction(String name, boolean start) {
        this.name = name;
        this.start = start;
    }

    @Override
    public void call(GlobalEvent event) {
        event.action(name, start);
    }
}
