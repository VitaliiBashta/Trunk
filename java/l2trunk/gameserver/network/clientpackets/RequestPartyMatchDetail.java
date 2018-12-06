package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

public final class RequestPartyMatchDetail extends L2GameClientPacket {
    private int roomId;
    private int locations;
    private int level;

    /**
     * Format: dddd
     */
    @Override
    protected void readImpl() {
        roomId = readD(); // room id, если 0 то autojoin
        locations = readD(); // location
        level = readD(); // 1 - all, 0 - my level (только при autojoin)
        //readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (player.getMatchingRoom() != null)
            return;

        if (roomId > 0) {
            MatchingRoom room = MatchingRoomManager.INSTANCE.getMatchingRoom(MatchingRoom.PARTY_MATCHING, roomId);
            if (room == null)
                return;

            room.addMember(player);
        } else {
            for (MatchingRoom room : MatchingRoomManager.INSTANCE.getMatchingRooms(MatchingRoom.PARTY_MATCHING, locations, level == 1, player))
                if (room.addMember(player))
                    break;
        }
    }
}