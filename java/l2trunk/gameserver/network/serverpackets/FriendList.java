package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FriendList extends L2GameServerPacket {
    private List<FriendInfo> friends;

    public FriendList(Player player) {
        Map<Integer, Friend> friends = player.getFriendList().getList();
        this.friends = new ArrayList<>(friends.size());
        friends.forEach( (k,v) -> {
            FriendInfo f = new FriendInfo();
            f.name = v.getName();
            f.classId = v.getClassId();
            f.objectId = k;
            f.level = v.getLevel();
            f.online = v.isOnline();
            this.friends.add(f);
        });
    }

    @Override
    protected void writeImpl() {
        writeC(0x58);
        writeD(friends.size());
        friends.forEach(f -> {
            writeD(f.objectId);
            writeS(f.name);
            writeD(f.online);
            writeD(f.online ? f.objectId : 0);
            writeD(f.classId);
            writeD(f.level);
        });
    }

    private class FriendInfo {
        private String name;
        private int objectId;
        private boolean online;
        private int level;
        private int classId;
    }
}
