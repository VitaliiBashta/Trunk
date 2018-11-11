package l2trunk.gameserver.network.serverpackets;

public class ExVariationResult extends L2GameServerPacket {
    private final int _stat12;
    private final int _stat34;
    private final int _unk3;

    public ExVariationResult(int unk1, int unk2, int unk3) {
        _stat12 = unk1;
        _stat34 = unk2;
        _unk3 = unk3;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x56);
        writeD(_stat12);
        writeD(_stat34);
        writeD(_unk3);
    }
}