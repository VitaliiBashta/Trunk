package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInstance;

import java.util.List;

public final class ExBR_AgathionEnergyInfo extends L2GameServerPacket {
    private final int _size;
    private List<ItemInstance> _itemList;

    public ExBR_AgathionEnergyInfo(int size, List<ItemInstance> item) {
        _itemList = item;
        _size = size;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xDE);
        writeD(_size);
        for (ItemInstance item : _itemList) {
            if (item.getTemplate().getAgathionEnergy() == 0)
                continue;
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(0x200000);
            writeD(item.getAgathionEnergy());//current energy
            writeD(item.getTemplate().getAgathionEnergy());   //max energy
        }
    }
}