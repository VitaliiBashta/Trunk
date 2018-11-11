package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SSQStatus;
import l2trunk.gameserver.network.serverpackets.ShowXMasSeal;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.List;

public final class Books extends SimpleItemHandler implements ScriptFile {
    private static final Integer[] ITEM_IDS = {5555, 5707};


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
    public List<Integer> getItemIds() {
        return Arrays.asList(ITEM_IDS);
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        switch (itemId) {
            case 5555:
                player.sendPacket(new ShowXMasSeal(5555));
                break;
            case 5707:
                player.sendPacket(new SSQStatus(player, 1));
                break;
            default:
                return false;
        }

        return true;
    }
}