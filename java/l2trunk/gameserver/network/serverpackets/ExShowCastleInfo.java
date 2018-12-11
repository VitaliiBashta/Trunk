package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.List;


public final class ExShowCastleInfo extends L2GameServerPacket {
    private List<CastleInfo> _infos;

    public ExShowCastleInfo() {
        String ownerName;
        int id, tax, nextSiege;

        List<Castle> castles = ResidenceHolder.getResidenceList(Castle.class);
        _infos = new ArrayList<>(castles.size());
        for (Castle castle : castles) {
            ownerName = ClanTable.INSTANCE.getClanName(castle.getOwnerId());
            id = castle.getId();
            tax = castle.getTaxPercent();
            nextSiege = (int) (castle.getSiegeDate().getTimeInMillis() / 1000);
            _infos.add(new CastleInfo(ownerName, id, tax, nextSiege));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x14);
        writeD(_infos.size());
        for (CastleInfo info : _infos) {
            writeD(info.id);
            writeS(info.ownerName);
            writeD(info.tax);
            writeD(info.nextSiege);
        }
        _infos.clear();
    }

    private static class CastleInfo {
        final String ownerName;
        final int id;
        final int tax;
        final int nextSiege;

        CastleInfo(String ownerName, int id, int tax, int nextSiege) {
            this.ownerName = ownerName;
            this.id = id;
            this.tax = tax;
            this.nextSiege = nextSiege;
        }
    }
}