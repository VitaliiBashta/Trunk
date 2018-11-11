package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

import java.util.Map;


public class ExReceiveShowPostFriend extends L2GameServerPacket {
    private final Map<Integer,String> _list;

    public ExReceiveShowPostFriend(Player player) {
        _list = player.getPostFriends();
    }

    @Override
    public void writeImpl() {
        writeEx(0xD3);
        writeD(_list.size());
        for (String t : _list.values())
            writeS(t);
    }
}
