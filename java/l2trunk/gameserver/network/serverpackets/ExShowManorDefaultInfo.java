package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Manor;

import java.util.List;

/**
 * format(packet 0xFE)
 * ch cd [ddddcdcd]
 * c  - id
 * h  - sub id
 * <p>
 * c
 * d  - size
 * <p>
 * [
 * d  - level
 * d  - seed price
 * d  - seed level
 * d  - crop price
 * c
 * d  - reward 1 id
 * c
 * d  - reward 2 id
 * ]
 */
public final class ExShowManorDefaultInfo extends L2GameServerPacket {
    private List<Integer> _crops;

    public ExShowManorDefaultInfo() {
        _crops = Manor.INSTANCE.getAllCrops();
    }

    @Override
    protected void writeImpl() {
        writeEx(0x25);
        writeC(0);
        writeD(_crops.size());
        for (int cropId : _crops) {
            writeD(cropId); // crop Id
            writeD(Manor.INSTANCE.getSeedLevelByCrop(cropId)); // level
            writeD(Manor.INSTANCE.getSeedBasicPriceByCrop(cropId)); // seed price
            writeD(Manor.INSTANCE.getCropBasicPrice(cropId)); // crop price
            writeC(1); // rewrad 1 Type
            writeD(Manor.INSTANCE.getRewardItem(cropId, 1)); // Rewrad 1 Type Item Id
            writeC(1); // rewrad 2 Type
            writeD(Manor.INSTANCE.getRewardItem(cropId, 2)); // Rewrad 2 Type Item Id
        }
    }
}