package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PledgeReceiveWarList;

public class RequestPledgeWarList extends L2GameClientPacket {
    // format: (ch)dd
    private static int _type;
    private int _page;

    @Override
    protected void readImpl() {
        _page = readD();
        _type = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Clan clan = activeChar.getClan();
        if (clan != null)
            activeChar.sendPacket(new PledgeReceiveWarList(clan, _type, _page));
    }
}