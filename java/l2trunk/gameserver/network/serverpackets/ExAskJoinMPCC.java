package l2trunk.gameserver.network.serverpackets;

/**
 * Asks the player to join a Command Channel
 */
public final class ExAskJoinMPCC extends L2GameServerPacket {
    private final String requestorName;

    public ExAskJoinMPCC(String requestorName) {
        this.requestorName = requestorName;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x1a);
        writeS(requestorName); // лидер CC
        writeD(0x00);
    }
}