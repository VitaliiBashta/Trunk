package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;
import l2trunk.gameserver.utils.ItemFunctions;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket {
    private int _itemId;
    private int _catalystId;

    @Override
    protected void readImpl() {
        _catalystId = readD();
        _itemId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        PcInventory inventory = activeChar.getInventory();
        ItemInstance itemToEnchant = inventory.getItemByObjectId(_itemId);
        ItemInstance catalyst = inventory.getItemByObjectId(_catalystId);

        if (ItemFunctions.checkCatalyst(itemToEnchant, catalyst))
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(1));
        else
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
    }
}