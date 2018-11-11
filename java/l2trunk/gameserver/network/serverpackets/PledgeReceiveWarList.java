package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.Clan;

import java.util.ArrayList;
import java.util.List;


public class PledgeReceiveWarList extends L2GameServerPacket {
    private final List<WarInfo> infos = new ArrayList<>();
    private final int _updateType;
    @SuppressWarnings("unused")
    private final int _page;

    public PledgeReceiveWarList(Clan clan, int type, int page) {
        _updateType = type;
        _page = page;

        List<Clan> clans = _updateType == 1 ? clan.getAttackerClans() : clan.getEnemyClans();
        for (Clan _clan : clans) {
            if (_clan == null)
                continue;
            infos.add(new WarInfo(_clan.getName(), _updateType, 0));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3f);
        writeD(_updateType); //which type of war list sould be revamped by this packet
        writeD(0x00); //page number goes here(_page ), made it static cuz not sure how many war to add to one page so TODO here
        writeD(infos.size());
        for (WarInfo _info : infos) {
            writeS(_info.clan_name);
            writeD(_info.unk1);
            writeD(_info.unk2); //filler ??
        }
    }

    static class WarInfo {
        final String clan_name;
        final int unk1;
        final int unk2;

        WarInfo(String _clan_name, int _unk1, int _unk2) {
            clan_name = _clan_name;
            unk1 = _unk1;
            unk2 = _unk2;
        }
    }
}