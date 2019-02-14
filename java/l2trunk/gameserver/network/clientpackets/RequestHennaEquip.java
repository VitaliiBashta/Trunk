package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.Henna;

public final class RequestHennaEquip extends L2GameClientPacket {
    private int symbolId;

    @Override
    protected void readImpl() {
        symbolId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Henna temp = HennaHolder.getHenna(symbolId);
        if (temp == null || !temp.isForThisClass(player)) {
            player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
            return;
        }

        long countDye = player.getInventory().getCountOf(temp.dyeId);

        if (countDye >= temp.drawCount && player.haveAdena( temp.price)) {
            if (player.consumeItem(temp.dyeId, temp.drawCount) && player.reduceAdena(temp.price, "RequestHennaEquip")) {
                player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
                player.addHenna(temp);
            }
        } else
            player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
    }
}