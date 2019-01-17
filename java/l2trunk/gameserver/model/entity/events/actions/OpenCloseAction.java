package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public final class OpenCloseAction implements EventAction {
    private final boolean open;
    private final String name;

    public OpenCloseAction(boolean open, String name) {
        this.open = open;
        this.name = name;
    }

    @Override
    public void call(GlobalEvent event) {
        event.doorAction(name, open);
    }
}
