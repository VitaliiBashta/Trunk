package l2trunk.gameserver.network.serverpackets;

public final class ShowCalc extends L2GameServerPacket {
    private final int calculatorId;

    public ShowCalc(int calculatorId) {
        this.calculatorId = calculatorId;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xe2);
        writeD(calculatorId);
    }
}