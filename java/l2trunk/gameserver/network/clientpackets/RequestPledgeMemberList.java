package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;

public final class RequestPledgeMemberList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        Clan clan = activeChar.getClan();
        if (clan != null) {
            activeChar.sendPacket(clan.listAll());
        }
    }
}