package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.residence.Dominion;

import java.util.ArrayList;
import java.util.List;


public final class ExReplyDominionInfo extends L2GameServerPacket {
    private List<TerritoryInfo> dominionList;

    public ExReplyDominionInfo() {
        List<Dominion> dominions = ResidenceHolder.getResidenceList(Dominion.class);
        dominionList = new ArrayList<>(dominions.size());

        for (Dominion dominion : dominions) {
            if (dominion.getSiegeDate().getTimeInMillis() == 0)
                continue;

            dominionList.add(new TerritoryInfo(dominion.getId(), dominion.getName(), dominion.getOwner().getName(), dominion.getFlags(), (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L)));
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0x92);
        writeD(dominionList.size());
        dominionList.forEach(cf -> {
            writeD(cf.id);
            writeS(cf.terr);
            writeS(cf.clan);
            writeD(cf.flags.size());
            cf.flags.forEach(this::writeD);
            writeD(cf.startTime);
        });
    }

    private class TerritoryInfo {
        final int id;
        final String terr;
        final String clan;
        final List<Integer> flags;
        final int startTime;

        TerritoryInfo(int id, String terr, String clan, List<Integer> flags, int startTime) {
            this.id = id;
            this.terr = terr;
            this.clan = clan;
            this.flags = flags;
            this.startTime = startTime;
        }
    }
}