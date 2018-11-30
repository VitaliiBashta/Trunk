package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.Warehouse.ItemClassComparator;
import l2trunk.gameserver.model.items.Warehouse.WarehouseType;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.item.ItemTemplate.ItemClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WareHouseWithdrawList extends L2GameServerPacket {
    private final long _adena;
    private final int _type;
    private List<ItemInfo> _itemList = new ArrayList<>();

    public WareHouseWithdrawList(Player player, WarehouseType type, ItemClass clss) {
        _adena = player.getAdena();
        _type = type.ordinal();

        ItemInstance[] items;
        switch (type) {
            case PRIVATE:
                items = player.getWarehouse().getItems(clss);
                break;
            case FREIGHT:
                items = player.getFreight().getItems(clss);
                break;
            case CLAN:
            case CASTLE:
                items = player.getClan().getWarehouse().getItems(clss);
                break;
            default:
                _itemList = Collections.emptyList();
                return;
        }

        _itemList = new ArrayList<>(items.length);
        ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
        for (ItemInstance item : items)
            _itemList.add(new ItemInfo(item));
    }

    public WareHouseWithdrawList(Clan clan, ItemClass clss) {
        _adena = 0;
        _type = WarehouseType.CLAN.ordinal();

        ItemInstance[] items = clan == null ? new ItemInstance[0] : clan.getWarehouse().getItems();

        _itemList = new ArrayList<>(items.length);
        ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
        for (ItemInstance item : items)
            _itemList.add(new ItemInfo(item));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x42);
        writeH(_type);
        writeQ(_adena);
        writeH(_itemList.size());
        for (ItemInfo item : _itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}