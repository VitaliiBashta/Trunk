package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.Warehouse.ItemClassComparator;
import l2trunk.gameserver.model.items.Warehouse.WarehouseType;

import java.util.ArrayList;
import java.util.List;


public class WareHouseDepositList extends L2GameServerPacket {
    private final int _whtype;
    private final long _adena;
    private final List<ItemInfo> _itemList;

    public WareHouseDepositList(Player cha, WarehouseType whtype) {
        _whtype = whtype.ordinal();
        _adena = cha.getAdena();

        List<ItemInstance> items = cha.getInventory().getItems();
        items.sort(ItemClassComparator.getInstance());
        _itemList = new ArrayList<>(items.size());
        for (ItemInstance item : items)
            if (_whtype == 1) {
                if (item.canBeStored(cha, true))
                    _itemList.add(new ItemInfo(item));
            } else if (_whtype == 2) {
                if (item.canBeStored(cha, false))
                    _itemList.add(new ItemInfo(item));
            } else if (_whtype == 3) {
                if (item.canBeStored(cha, false))
                    _itemList.add(new ItemInfo(item));
            } else if (_whtype == WarehouseType.FREIGHT.ordinal()) {
                if (item.canBeTraded(cha) && !item.isStackable() || item.getCrystalType().externalOrdinal >= 4)
                    _itemList.add(new ItemInfo(item));
            }
    }

    @Override
    protected final void writeImpl() {
        writeC(0x41);
        writeH(_whtype);
        writeQ(_adena);
        writeH(_itemList.size());
        for (ItemInfo item : _itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}