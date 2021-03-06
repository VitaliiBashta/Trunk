package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ExShowProcureCropDetail extends L2GameServerPacket {
    private final int cropId;
    private final Map<Integer, CropProcure> castleCrops;

    public ExShowProcureCropDetail(int cropId) {
        this.cropId = cropId;
        castleCrops = new TreeMap<>();

        for (Castle c : ResidenceHolder.getCastles()) {
            CropProcure cropItem = c.getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
            if (cropItem != null && cropItem.getAmount() > 0)
                castleCrops.put(c.getId(), cropItem);
        }
    }

    @Override
    public void writeImpl() {
        writeEx(0x78);

        writeD(cropId); // crop id
        writeD(castleCrops.size()); // size

        castleCrops.keySet().forEach(manorId -> {
            CropProcure crop = castleCrops.get(manorId);
            writeD(manorId); // manor name
            writeQ(crop.getAmount()); // buy residual
            writeQ(crop.price); // buy price
            writeC(crop.getReward()); // reward type
        });
    }
}