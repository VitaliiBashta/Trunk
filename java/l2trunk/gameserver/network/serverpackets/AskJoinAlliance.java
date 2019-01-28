package l2trunk.gameserver.network.serverpackets;

public final class AskJoinAlliance extends L2GameServerPacket {
    private final String requestorName;
    private final String requestorAllyName;
    private final int requestorId;

    public AskJoinAlliance(int requestorId, String requestorName, String requestorAllyName) {
        this.requestorName = requestorName;
        this.requestorAllyName = requestorAllyName;
        this.requestorId = requestorId;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xbb);
        writeD(requestorId);
        writeS(requestorName);
        writeS("");
        writeS(requestorAllyName);
    }
}