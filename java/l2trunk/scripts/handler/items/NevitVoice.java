package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Collections;
import java.util.List;

public final class NevitVoice extends SimpleItemHandler implements ScriptFile {
    private static final int ITEM_IDS = 17094;

    @Override
    public List<Integer> getItemIds() {
        return Collections.singletonList(ITEM_IDS);
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (!useItem(player, item, 1))
            return false;

        switch (itemId) {
            case 17094:
                player.addRecomHave(10);
                break;
            default:
                return false;
        }

        return true;
    }
}
