package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

import java.util.Map;


public class ExReceiveShowPostFriend extends L2GameServerPacket {
    private Map<Integer,String> _list;

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
