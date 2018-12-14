package l2trunk.gameserver.network.serverpackets;

public final class AskJoinParty extends L2GameServerPacket {
    private final String requestorName;
    private final int itemDistribution;

    public AskJoinParty(String requestorName, int itemDistribution) {
        this.requestorName = requestorName;
        this.itemDistribution = itemDistribution;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x39);
        writeS(requestorName);
        writeD(itemDistribution);
    }
}