package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.CommandChannel;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;


public class ExMultiPartyCommandChannelInfo extends L2GameServerPacket {
    private final String ChannelLeaderName;
    private final int MemberCount;
    private final List<ChannelPartyInfo> parties;

    public ExMultiPartyCommandChannelInfo(CommandChannel channel) {
        ChannelLeaderName = channel.getLeader().getName();
        MemberCount = channel.size();

        parties = new ArrayList<>();
        for (Party party : channel.getParties()) {
            Player leader = party.getLeader();
            if (leader != null)
                parties.add(new ChannelPartyInfo(leader.getName(), leader.objectId(), party.size()));
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0x31);
        writeS(ChannelLeaderName); // имя лидера CC
        writeD(0); // Looting type?
        writeD(MemberCount); // общее число человек в СС
        writeD(parties.size()); // общее число партий в СС

        for (ChannelPartyInfo party : parties) {
            writeS(party.Leader_name); // имя лидера партии
            writeD(party.Leader_obj_id); // ObjId пати лидера
            writeD(party.MemberCount); // количество мемберов в пати
        }
    }

    static class ChannelPartyInfo {
        final String Leader_name;
        final int Leader_obj_id;
        final int MemberCount;

        ChannelPartyInfo(String _Leader_name, int _Leader_obj_id, int _MemberCount) {
            Leader_name = _Leader_name;
            Leader_obj_id = _Leader_obj_id;
            MemberCount = _MemberCount;
        }
    }
}