package l2trunk.gameserver.network.serverpackets;

public class AskJoinPledge extends L2GameServerPacket {
    private final int _requestorId;
    private final String _pledgeName;

    public AskJoinPledge(int requestorId, String pledgeName) {
        _requestorId = requestorId;
        _pledgeName = pledgeName;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x2c);
        writeD(_requestorId);
        writeS(_pledgeName);
    }
}