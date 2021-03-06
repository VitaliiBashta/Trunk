package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ExListMpccWaiting extends L2GameServerPacket {
    private static final int PAGE_SIZE = 10;
    private final int _fullSize;
    private final List<MatchingRoom> _list;

    public ExListMpccWaiting(Player player, int page, int location, boolean allLevels) {
        int first = (page - 1) * PAGE_SIZE;
        int firstNot = page * PAGE_SIZE;
        int i = 0;
        Collection<MatchingRoom> all = MatchingRoomManager.INSTANCE.getMatchingRooms(MatchingRoom.CC_MATCHING, location, allLevels, player);
        _fullSize = all.size();
        _list = new ArrayList<>(PAGE_SIZE);
        for (MatchingRoom c : all) {
            if (i < first || i >= firstNot)
                continue;

            _list.add(c);
            i++;
        }
    }

    @Override
    public void writeImpl() {
        writeEx(0x9C);
        writeD(_fullSize);
        writeD(_list.size());
        for (MatchingRoom room : _list) {
            writeD(room.getId());
            writeS(room.getTopic());
            writeD(room.getPlayers().size());
            writeD(room.getMinLevel());
            writeD(room.getMaxLevel());
            writeD(1);  //min group
            writeD(room.getMaxMembersSize());   //max group
            Player leader = room.getLeader();
            writeS(leader == null ? StringUtils.EMPTY : leader.getName());
        }
    }
}
