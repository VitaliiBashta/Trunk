package l2trunk.gameserver.model.items.listeners;

import l2trunk.gameserver.listener.inventory.OnEquipListener;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.stats.funcs.Func;

public final class StatsListener implements OnEquipListener {
    private static final StatsListener _instance = new StatsListener();

    public static StatsListener getInstance() {
        return _instance;
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        actor.removeStatsOwner(item);
        actor.updateStats();
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        Func[] funcs = item.getStatFuncs();
        actor.addStatFuncs(funcs);
        actor.updateStats();
    }
}