package l2trunk.gameserver.network.serverpackets;

public class RestartResponse extends L2GameServerPacket {
    public static final RestartResponse OK = new RestartResponse(1), FAIL = new RestartResponse(0);
    private final String _message;
    private final int _param;

    private RestartResponse(int param) {
        _message = "bye";
        _param = param;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x71);
        writeD(_param); //01-ok
        writeS(_message);
    }
}