package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;

public class RequestOlympiadObserverEnd extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        if (activeChar.getObserverMode() == Player.OBSERVER_STARTED)
            if (activeChar.getOlympiadObserveGame() != null)
                activeChar.leaveOlympiadObserverMode(true);
    }
}