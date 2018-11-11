package l2trunk.gameserver.network.serverpackets;

/**
 * Send Private (Friend) Message
 * <p>
 * Format: c dSSS
 * <p>
 * d: Unknown
 * S: Sending Player
 * S: Receiving Player
 * S: Message
 */
public class L2FriendSay extends L2GameServerPacket {
    private final String _sender;
    private final String _receiver;
    private final String _message;

    public L2FriendSay(String sender, String reciever, String message) {
        _sender = sender;
        _receiver = reciever;
        _message = message;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x78);
        writeD(0);
        writeS(_receiver);
        writeS(_sender);
        writeS(_message);
    }
}