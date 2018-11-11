package l2trunk.gameserver.network.serverpackets;

public class ExVariationCancelResult extends L2GameServerPacket {
    private final int _closeWindow;
    private final int _unk1;

    public ExVariationCancelResult(int result) {
        _closeWindow = 1;
        _unk1 = result;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x58);
        writeD(_unk1);
        writeD(_closeWindow);
    }
}