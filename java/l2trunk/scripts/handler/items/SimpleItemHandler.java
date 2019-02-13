package l2trunk.scripts.handler.items;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

abstract class SimpleItemHandler extends ScriptItemHandler {
    static boolean useItem(Player player, ItemInstance item, long count) {
        if (player.getInventory().destroyItem(item, count, "ItemUse")) {
            player.sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(item.getItemId()));
            return true;
        }

        player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        return false;
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        if (player.isInFlyingTransform()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        return useItemImpl(player, item, ctrl);
    }

    protected abstract boolean useItemImpl(Player player, ItemInstance item, boolean ctrl);
}
