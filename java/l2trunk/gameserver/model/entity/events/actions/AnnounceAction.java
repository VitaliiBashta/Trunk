package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public final class AnnounceAction implements EventAction {
    private final int id;

    public AnnounceAction(int id) {
        this.id = id;
    }

    @Override
    public void call(GlobalEvent event) {
        event.announce(id);
    }
}
