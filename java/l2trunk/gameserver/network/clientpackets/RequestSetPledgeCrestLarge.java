package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public class RequestSetPledgeCrestLarge extends L2GameClientPacket {
    private int _length;
    private byte[] _data;

    /**
     * format: chd(buffPrice)
     */
    @Override
    protected void readImpl() {
        _length = readD();
        if (_length == CrestCache.LARGE_CREST_SIZE && _length == buf.remaining()) {
            _data = new byte[_length];
            readB(_data);
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Clan clan = activeChar.getClan();
        if (clan == null)
            return;

        if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST) {
            if (clan.getCastle() == 0 && clan.getHasHideout() == 0) {
                activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
                return;
            }

            int crestId = 0;

            if (_data != null) {
                crestId = CrestCache.savePledgeCrestLarge(clan.clanId(), _data);
                activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
            } else if (clan.hasCrestLarge())
                CrestCache.removePledgeCrestLarge(clan.clanId());

            clan.setCrestLargeId(crestId);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}