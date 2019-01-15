package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;

public final class SnoopQuit extends L2GameClientPacket {
    private int snoopID;

    /**
     * format: cd
     */
    @Override
    protected void readImpl() {
        snoopID = readD();
    }

    @Override
    protected void runImpl() {
        Player player = (Player) GameObjectsStorage.findObject(snoopID);
        if (player == null)
            return;
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        player.removeSnooper(activeChar);
        activeChar.removeSnooped(player);
    }
}