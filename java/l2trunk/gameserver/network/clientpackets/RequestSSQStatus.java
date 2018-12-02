package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.network.serverpackets.SSQStatus;

/**
 * Seven Signs Record Update Request
 * packet type id 0xc8
 * format: cc
 */
public final class RequestSSQStatus extends L2GameClientPacket {
    private int page;

    @Override
    protected void readImpl() {
        page = readC();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if ((SevenSigns.INSTANCE.isSealValidationPeriod() || SevenSigns.INSTANCE.isCompResultsPeriod()) && page == 4)
            return;

        activeChar.sendPacket(new SSQStatus(activeChar, page));
    }
}