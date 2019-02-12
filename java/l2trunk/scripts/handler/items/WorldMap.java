package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ShowMiniMap;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class WorldMap extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private static final List<Integer> ITEM_IDS = List.of(1665, 1863, 9994);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        player.sendPacket(new ShowMiniMap(item.getItemId()));
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}