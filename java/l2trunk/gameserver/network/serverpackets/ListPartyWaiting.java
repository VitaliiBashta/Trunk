package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Format:(c) dddddds
 */
public class ListPartyWaiting extends L2GameServerPacket {
    private final Collection<MatchingRoom> _rooms;
    private final int _fullSize;

    public ListPartyWaiting(int region, boolean allLevels, int page, Player activeChar) {
        int first = (page - 1) * 64;
        int firstNot = page * 64;
        _rooms = new ArrayList<>();

        int i = 0;
        List<MatchingRoom> temp = MatchingRoomManager.INSTANCE.getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);
        _fullSize = temp.size();
        for (MatchingRoom room : temp) {
            if (i < first || i >= firstNot)
                continue;
            _rooms.add(room);
            i++;
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0x9c);
        writeD(_fullSize);
        writeD(_rooms.size());

        for (MatchingRoom room : _rooms) {
            writeD(room.getId()); //room id
            writeS(room.getLeader() == null ? "None" : room.getLeader().getName());
            writeD(room.getLocationId());
            writeD(room.getMinLevel()); //min occupation
            writeD(room.getMaxLevel()); //max occupation
            writeD(room.getMaxMembersSize()); //max members coun
            writeS(room.getTopic()); // room name

            Collection<Player> players = room.getPlayers();
            writeD(players.size()); //members count
            for (Player player : players) {
                writeD(player.getClassId().id);
                writeS(player.getName());
            }
        }
    }
}