package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExGetBookMarkInfo;

public class RequestBookMarkSlotInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //just trigger
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
    }
}