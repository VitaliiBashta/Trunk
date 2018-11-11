package l2trunk.gameserver.network.serverpackets;

public class ExDuelAskStart extends L2GameServerPacket {
    private final String _requestor;
    private final int _isPartyDuel;

    public ExDuelAskStart(String requestor, int isPartyDuel) {
        _requestor = requestor;
        _isPartyDuel = isPartyDuel;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x4c);
        writeS(_requestor);
        writeD(_isPartyDuel);
    }
}