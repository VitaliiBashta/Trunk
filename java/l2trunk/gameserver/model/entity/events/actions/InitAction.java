package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public final class InitAction implements EventAction {
    private final String name;

    public InitAction(String name) {
        this.name = name;
    }

    @Override
    public void call(GlobalEvent event) {
        event.initAction(name);
    }
}
