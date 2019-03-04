package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.tables.ClanTable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExShowCastleInfo extends L2GameServerPacket {
    private List<CastleInfo> infos;

    public ExShowCastleInfo() {
        infos = ResidenceHolder.getCastles().stream().map(CastleInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x14);
        writeD(infos.size());
        infos.forEach(info -> {
            writeD(info.id);
            writeS(info.ownerName);
            writeD(info.tax);
            writeD(info.nextSiege);
        });
        infos.clear();
    }

    private static class CastleInfo {
        final String ownerName;
        final int id;
        final int tax;
        final int nextSiege;

        CastleInfo(Castle castle) {
            ownerName = ClanTable.INSTANCE.getClanName(castle.getOwnerId());
            id = castle.getId();
            tax = castle.getTaxPercent();
            nextSiege = (int) (castle.getSiegeDate().getTimeInMillis() / 1000);

        }
    }
}