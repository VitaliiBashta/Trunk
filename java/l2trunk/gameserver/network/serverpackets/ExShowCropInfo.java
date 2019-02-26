package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.List;


/**
 * Format:
 * cddd[ddddcdc[d]c[d]]
 * cddd[dQQQcdc[d]c[d]] - Gracia Final
 */

public final class ExShowCropInfo extends L2GameServerPacket {
    private final List<CropProcure> crops;
    private final int manorId;

    public ExShowCropInfo(int manorId, List<CropProcure> crops) {
        this.manorId = manorId;
        this.crops = crops;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x24); // SubId
        writeC(0);
        writeD(manorId); // Manor ID
        writeD(0);
        writeD(crops.size());
        for (CropProcure crop : crops) {
            writeD(crop.cropId); // Crop id
            writeQ(crop.getAmount()); // Buy residual
            writeQ(crop.getStartAmount()); // Buy
            writeQ(crop.price); // Buy price
            writeC(crop.getReward()); // Reward
            writeD(Manor.INSTANCE.getSeedLevelByCrop(crop.cropId)); // Seed Level

            writeC(1); // rewrad 1 Type
            writeD(Manor.INSTANCE.getRewardItem(crop.cropId, 1)); // Rewrad 1 Type Item Id

            writeC(1); // rewrad 2 Type
            writeD(Manor.INSTANCE.getRewardItem(crop.cropId, 2)); // Rewrad 2 Type Item Id
        }
    }
}