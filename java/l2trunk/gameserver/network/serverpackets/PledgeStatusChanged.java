package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.Clan;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00    ....H...........
 * 0010: 00 00 00 00 00                                     .....
 * <p>
 * format   ddddd
 */
public class PledgeStatusChanged extends L2GameServerPacket {
    private final int leader_id;
    private final int clan_id;
    private final int level;

    public PledgeStatusChanged(Clan clan) {
        leader_id = clan.getLeaderId();
        clan_id = clan.clanId();
        level = clan.getLevel();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xCD);
        writeD(leader_id);
        writeD(clan_id);
        writeD(0);
        writeD(level);
        writeD(0);
        writeD(0);
        writeD(0);
    }
}