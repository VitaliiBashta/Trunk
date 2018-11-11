package l2trunk.gameserver.network.serverpackets;

public class ExAskModifyPartyLooting extends L2GameServerPacket {
    private final String _requestor;
    private final int _mode;

    public ExAskModifyPartyLooting(String name, int mode) {
        _requestor = name;
        _mode = mode;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xBF);
        writeS(_requestor);
        writeD(_mode);
    }
}
