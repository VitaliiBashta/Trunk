package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.PremiumItem;
import l2trunk.gameserver.network.serverpackets.ExGetPremiumItemList;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

//FIXME item-API
public final class RequestWithDrawPremiumItem extends L2GameClientPacket {
    private int itemNum;
    private int charId;
    private long itemcount;

    @Override
    protected void readImpl() {
        itemNum = readD();
        charId = readD();
        itemcount = readQ();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }
        if (itemcount <= 0) {
            return;
        }

        if (activeChar.objectId() != charId) {
            // audit
            return;
        }
        if (activeChar.getPremiumItemList().isEmpty()) {
            // audit
            return;
        }
        if ((activeChar.getWeightPenalty() >= 3) || ((activeChar.getInventoryLimit() * 0.8) <= activeChar.getInventory().getSize())) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT);
            return;
        }
        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
            return;
        }

        PremiumItem item = activeChar.getPremiumItemList().get(itemNum);
        if (item == null) {
            return;
        }
        boolean stackable = ItemHolder.getTemplate(item.getItemId()).stackable();
        if (item.getCount() < itemcount) {
            return;
        }
        if (!stackable) {
            for (int i = 0; i < itemcount; i++) {
                addItem(activeChar, item.getItemId(), 1);
            }
        } else {
            addItem(activeChar, item.getItemId(), itemcount);
        }
        if (itemcount < item.getCount()) {
            activeChar.getPremiumItemList().get(itemNum).updateCount(item.getCount() - itemcount);
            activeChar.updatePremiumItem(itemNum, item.getCount() - itemcount);
        } else {
            activeChar.getPremiumItemList().remove(itemNum);
            activeChar.deletePremiumItem(itemNum);
        }

        if (activeChar.getPremiumItemList().isEmpty()) {
            activeChar.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
        } else {
            activeChar.sendPacket(new ExGetPremiumItemList(activeChar));
        }
    }

    private void addItem(Player player, int itemId, long count) {
        player.getInventory().addItem(itemId, count, "RequestWithDrawPremiumItem");
        player.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
    }
}