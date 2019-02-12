package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SSQStatus;
import l2trunk.gameserver.network.serverpackets.ShowXMasSeal;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class Books extends SimpleItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(5555, 5707);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
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
