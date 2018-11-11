package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.Clan;

import java.util.List;

public final class ShowSiegeKillResults extends L2GameServerPacket {
    private final List<Clan> clans;

    public ShowSiegeKillResults(List<Clan> bestClans) {
        clans = bestClans;
    }

    @Override
    public void writeImpl() {
        writeEx(0x89);
        writeD(0x00); // Open/Dont Open
        writeD(clans.size());
        for (Clan c : clans) {
            writeS(c == null ? "" : c.getName());
            writeD(c == null ? 0 : c.getSiegeKills());
        }
    }
}