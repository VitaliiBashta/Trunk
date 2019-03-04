package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.residence.Dominion;

import java.util.List;
import java.util.stream.Collectors;

public final class ExReplyDominionInfo extends L2GameServerPacket {
    private List<TerritoryInfo> dominionList;

    public ExReplyDominionInfo() {
        dominionList = ResidenceHolder.getDominions().stream()
                .filter(dominion -> dominion.getSiegeDate().getTimeInMillis() != 0)
                .map(TerritoryInfo::new)
                .collect(Collectors.toList());
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

        TerritoryInfo(Dominion dominion) {
            id = dominion.getId();
            terr = dominion.getName();
            clan = dominion.getOwner().getName();
            flags = dominion.getFlags();
            startTime = (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L);
        }
    }
}