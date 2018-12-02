package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ShowMiniMap;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.List;

public final class WorldMap extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private static final Integer[] _itemIds = {1665, 1863, 9994};

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        player.sendPacket(new ShowMiniMap(player, item.getItemId()));
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return Arrays.asList(_itemIds);
    }
}