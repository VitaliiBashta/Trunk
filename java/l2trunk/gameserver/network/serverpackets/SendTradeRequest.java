package l2trunk.gameserver.network.serverpackets;

public class SendTradeRequest extends L2GameServerPacket {
    private final int _senderId;

    public SendTradeRequest(int senderId) {
        _senderId = senderId;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x70);
        writeD(_senderId);
    }
}