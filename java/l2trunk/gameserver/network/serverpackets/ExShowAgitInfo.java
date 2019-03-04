package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2trunk.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ExShowAgitInfo extends L2GameServerPacket {
    private List<AgitInfo> clanHalls;

    public ExShowAgitInfo() {
        List<ClanHall> chs = ResidenceHolder.getClanHalls();
        clanHalls = new ArrayList<>(chs.size());

        for (ClanHall clanHall : chs) {
            int ch_id = clanHall.getId();
            int getType;
            if (clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class)
                getType = 0;
            else if (clanHall.getSiegeEvent().getClass() == ClanHallMiniGameEvent.class)
                getType = 2;
            else
                getType = 1;

            Clan clan = ClanTable.INSTANCE.getClan(clanHall.getOwnerId());
            String clan_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getName();
            String leader_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getLeaderName();
            clanHalls.add(new AgitInfo(clan_name, leader_name, ch_id, getType));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x16);
        writeD(clanHalls.size());
        for (AgitInfo info : clanHalls) {
            writeD(info.ch_id);
            writeS(info.clan_name);
            writeS(info.leader_name);
            writeD(info.getType);
        }
    }

    private static class AgitInfo {
        final String clan_name;
        final String leader_name;
        final int ch_id;
        final int getType;

        AgitInfo(String clan_name, String leader_name, int ch_id, int lease) {
            this.clan_name = clan_name;
            this.leader_name = leader_name;
            this.ch_id = ch_id;
            this.getType = lease;
        }
    }
}