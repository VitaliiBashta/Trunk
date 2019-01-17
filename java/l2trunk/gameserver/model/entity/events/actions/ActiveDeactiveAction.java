package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public final class ActiveDeactiveAction implements EventAction {
    private final boolean active;
    private final String name;

    public ActiveDeactiveAction(boolean active, String name) {
        this.active = active;
        this.name = name;
    }

    @Override
    public void call(GlobalEvent event) {
        event.zoneAction(name, active);
    }
}
