package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PledgeInfo;
import l2trunk.gameserver.tables.ClanTable;

public final class RequestPledgeInfo extends L2GameClientPacket {
    private int _clanId;

    @Override
    protected void readImpl() {
        _clanId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        if (_clanId < 10000000) {
            activeChar.sendActionFailed();
            return;
        }
        Clan clan = ClanTable.INSTANCE.getClan(_clanId);
        if (clan == null) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new PledgeInfo(clan));
    }
}