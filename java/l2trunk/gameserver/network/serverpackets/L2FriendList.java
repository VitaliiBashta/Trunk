package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.Friend;

import java.util.Map;

public final class L2FriendList extends L2GameServerPacket {
    private final Map<Integer, Friend> friendMap;

    public L2FriendList(Player player) {
        friendMap = player.getFriendList().getList();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x75);
        writeD(friendMap.size());

        friendMap.values().forEach(f -> {
            writeD(0);
            writeS(f.getName()); //name
            writeD(f.isOnline() ? 1 : 0); //online or offline
            writeD(f.getObjectId()); //object_id
        });
    }
}