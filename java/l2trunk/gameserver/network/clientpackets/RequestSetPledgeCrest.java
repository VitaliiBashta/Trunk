package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public class RequestSetPledgeCrest extends L2GameClientPacket {
    private int _length;
    private byte[] _data;

    @Override
    protected void readImpl() {
        _length = readD();
        if (_length == CrestCache.CREST_SIZE && _length == buf.remaining()) {
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
        if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST) {
            if (clan.getLevel() < 3) {
                activeChar.sendPacket(SystemMsg.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE);
                return;
            }

            int crestId = 0;

            if (_data != null)
                crestId = CrestCache.savePledgeCrest(clan.clanId(), _data);
            else if (clan.hasCrest())
                CrestCache.removePledgeCrest(clan.clanId());

            clan.setCrestId(crestId);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}