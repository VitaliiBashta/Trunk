package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket {
    private final RankPrivs[] _privs;

    public PledgePowerGradeList(RankPrivs[] privs) {
        _privs = privs;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3c);
        writeD(_privs.length);
        for (RankPrivs element : _privs) {
            writeD(element.getRank());
            writeD(element.getParty());
        }
    }
}