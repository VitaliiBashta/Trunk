package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SellListProcure extends L2GameServerPacket {
    private final long money;
    private final Map<ItemInstance, Long> _sellList = new HashMap<>();
    private final int castle;
    private List<CropProcure> _procureList;

    public SellListProcure(Player player, int castleId) {
        money = player.getAdena();
        castle = castleId;
        _procureList = ResidenceHolder.getCastle(castle).getCropProcure(0);
        for (CropProcure c : _procureList) {
            ItemInstance item = player.getInventory().getItemByItemId(c.cropId);
            if (item != null && c.getAmount() > 0)
                _sellList.put(item, c.getAmount());
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xef);
        writeQ(money);
        writeD(0x00); // lease ?
        writeH(_sellList.size()); // list size

        _sellList.keySet().forEach(item -> {
            writeH(item.getTemplate().getType1());
            writeD(item.objectId());
            writeD(item.getItemId());
            writeQ(_sellList.get(item));
            writeH(item.getTemplate().getType2ForPackets());
            writeH(0); // size of [dhhh]
            writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
        });
    }
}