package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

import java.util.List;

public class SetAccountInfo extends SendablePacket {
    private String _account;
    private int _size;
    private List<Integer> _deleteChars;

    public SetAccountInfo(String account, int size, List<Integer> deleteChars) {
        _account = account;
        _size = size;
        _deleteChars = deleteChars;
    }

    @Override
    protected void writeImpl() {
        writeC(0x05);
        writeS(_account);
        writeC(_size);
        writeD(_deleteChars.size());
        for (int i : _deleteChars)
            writeD(i);
    }
}
