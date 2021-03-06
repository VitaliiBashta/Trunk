package l2trunk.scripts.handler.items;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ExChooseInventoryAttributeItem;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class AttributeStones extends ScriptItemHandler implements ScriptFile {
    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            player.sendPacket(Msg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }

        if (player.getEnchantScroll() != null)
            return false;

        player.setEnchantScroll(item);
        player.setIsEnchantAllAttribute(ctrl);
        player.sendPacket(Msg.PLEASE_SELECT_ITEM_TO_ADD_ELEMENTAL_POWER);
        player.sendPacket(new ExChooseInventoryAttributeItem(item));
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return List.of(
                9546, 9547, 9548, 9549, 9550, 9551, 9552, 9553, 9554, 9555, 9556, 9557,
                9558, 9563, 9561, 9560, 9562, 9559, 9567, 9566, 9568, 9565, 9564, 9569,
                10521, 10522, 10523, 10524, 10525, 10526);
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

}