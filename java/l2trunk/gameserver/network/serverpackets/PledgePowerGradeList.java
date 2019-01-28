package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.RankPrivs;

import java.util.List;

public final class PledgePowerGradeList extends L2GameServerPacket {
    private final List<RankPrivs> privs;

    public PledgePowerGradeList(List<RankPrivs> privs) {
        this.privs = privs;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3c);
        writeD(privs.size());
        privs.forEach(element -> {
            writeD(element.getRank());
            writeD(element.getParty());
        });
    }
}