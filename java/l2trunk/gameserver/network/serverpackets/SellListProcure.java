package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellListProcure extends L2GameServerPacket {
    private final long _money;
    private final Map<ItemInstance, Long> _sellList = new HashMap<>();
    private final int _castle;
    private List<CropProcure> _procureList = new ArrayList<>();

    public SellListProcure(Player player, int castleId) {
        _money = player.getAdena();
        _castle = castleId;
        _procureList = ResidenceHolder.getInstance().getResidence(Castle.class, _castle).getCropProcure(0);
        for (CropProcure c : _procureList) {
            ItemInstance item = player.getInventory().getItemByItemId(c.getId());
            if (item != null && c.getAmount() > 0)
                _sellList.put(item, c.getAmount());
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xef);
        writeQ(_money);
        writeD(0x00); // lease ?
        writeH(_sellList.size()); // list size

        for (ItemInstance item : _sellList.keySet()) {
            writeH(item.getTemplate().getType1());
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeQ(_sellList.get(item));
            writeH(item.getTemplate().getType2ForPackets());
            writeH(0); // size of [dhhh]
            writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }
}