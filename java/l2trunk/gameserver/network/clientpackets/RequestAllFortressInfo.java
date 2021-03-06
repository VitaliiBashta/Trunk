package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExShowFortressInfo;

public class RequestAllFortressInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        activeChar.sendPacket(new ExShowFortressInfo());
    }
}