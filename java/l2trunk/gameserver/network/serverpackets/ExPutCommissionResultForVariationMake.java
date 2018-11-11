package l2trunk.gameserver.network.serverpackets;

public class ExPutCommissionResultForVariationMake extends L2GameServerPacket {
    private final int _gemstoneObjId;
    private final int _unk1;
    private final int _unk3;
    private final long _gemstoneCount;
    private final long _unk2;

    public ExPutCommissionResultForVariationMake(int gemstoneObjId, long count) {
        _gemstoneObjId = gemstoneObjId;
        _unk1 = 1;
        _gemstoneCount = count;
        _unk2 = 1;
        _unk3 = 1;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x55);
        writeD(_gemstoneObjId);
        writeD(_unk1);
        writeQ(_gemstoneCount);
        writeQ(_unk2);
        writeD(_unk3);
    }
}