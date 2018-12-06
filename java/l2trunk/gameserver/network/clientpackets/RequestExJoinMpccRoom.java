package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

public class RequestExJoinMpccRoom extends L2GameClientPacket {
    private int _roomId;

    @Override
    protected void readImpl() {
        _roomId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (player.getMatchingRoom() != null)
            return;

        MatchingRoom room = MatchingRoomManager.INSTANCE.getMatchingRoom(MatchingRoom.CC_MATCHING, _roomId);
        if (room == null)
            return;

        room.addMember(player);
    }
}