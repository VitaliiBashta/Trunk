package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public class GiveItemAction implements EventAction {
    private final int itemId;
    private final long count;

    public GiveItemAction(int itemId, long count) {
        this.itemId = itemId;
        this.count = count;
    }

    @Override
    public void call(GlobalEvent event) {
        event.itemObtainPlayers().forEach(p -> event.giveItem(p, itemId, count));
    }
}
