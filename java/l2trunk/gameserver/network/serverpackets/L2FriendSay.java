package l2trunk.gameserver.network.serverpackets;

public final class L2FriendSay extends L2GameServerPacket {
    private final String sender;
    private final String receiver;
    private final String message;

    public L2FriendSay(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x78);
        writeD(0);
        writeS(receiver);
        writeS(sender);
        writeS(message);
    }
}