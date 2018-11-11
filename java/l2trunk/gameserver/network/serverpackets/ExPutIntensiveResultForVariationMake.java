package l2trunk.gameserver.network.serverpackets;

public class ExPutIntensiveResultForVariationMake extends L2GameServerPacket {
    private final int _refinerItemObjId;
    private final int _lifestoneItemId;
    private final int _gemstoneItemId;
    private final int _unk;
    private final long _gemstoneCount;

    public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount) {
        _refinerItemObjId = refinerItemObjId;
        _lifestoneItemId = lifeStoneId;
        _gemstoneItemId = gemstoneItemId;
        _gemstoneCount = gemstoneCount;
        _unk = 1;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x54);
        writeD(_refinerItemObjId);
        writeD(_lifestoneItemId);
        writeD(_gemstoneItemId);
        writeQ(_gemstoneCount);
        writeD(_unk);
    }
}