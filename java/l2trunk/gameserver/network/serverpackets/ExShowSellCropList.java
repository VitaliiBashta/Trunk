package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * format
 * dd[dddc[d]c[d]dddcd]
 * dd[dddc[d]c[d]dQQcQ] - Gracia Final
 */
public class ExShowSellCropList extends L2GameServerPacket {
    private final Map<Integer, ItemInstance> cropsItems;
    private final Map<Integer, CropProcure> castleCrops;
    private int _manorId = 1;

    public ExShowSellCropList(Player player, int manorId, List<CropProcure> crops) {
        _manorId = manorId;
        castleCrops = new TreeMap<>();
        cropsItems = new TreeMap<>();

        List<Integer> allCrops = Manor.INSTANCE.getAllCrops();
        for (int cropId : allCrops) {
            ItemInstance item = player.getInventory().getItemByItemId(cropId);
            if (item != null)
                cropsItems.put(cropId, item);
        }

        for (CropProcure crop : crops)
            if (cropsItems.containsKey(crop.cropId) && crop.getAmount() > 0)
                castleCrops.put(crop.cropId, crop);

    }

    @Override
    public void writeImpl() {
        writeEx(0x2c);

        writeD(_manorId); // manor id
        writeD(cropsItems.size()); // size

        for (ItemInstance item : cropsItems.values()) {
            writeD(item.objectId()); // Object id
            writeD(item.getItemId()); // crop id
            writeD(Manor.INSTANCE.getSeedLevelByCrop(item.getItemId())); // seed level

            writeC(1);
            writeD(Manor.INSTANCE.getRewardItem(item.getItemId(), 1)); // reward 1 id

            writeC(1);
            writeD(Manor.INSTANCE.getRewardItem(item.getItemId(), 2)); // reward 2 id

            if (castleCrops.containsKey(item.getItemId())) {
                CropProcure crop = castleCrops.get(item.getItemId());
                writeD(_manorId); // manor
                writeQ(crop.getAmount()); // buy residual
                writeQ(crop.price); // buy price
                writeC(crop.getReward()); // reward
            } else {
                writeD(0xFFFFFFFF); // manor
                writeQ(0); // buy residual
                writeQ(0); // buy price
                writeC(0); // reward
            }
            writeQ(item.getCount()); // my crops
        }
    }
}