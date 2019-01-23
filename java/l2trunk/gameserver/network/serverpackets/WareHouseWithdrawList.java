package l2trunk.gameserver.network.serverpackets;

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
import java.util.stream.Collectors;


public final class WareHouseWithdrawList extends L2GameServerPacket {
    private final long adena;
    private final int type;
    private List<ItemInfo> itemList;

    public WareHouseWithdrawList(Player player, WarehouseType type, ItemClass clss) {
        adena = player.getAdena();
        this.type = type.ordinal();

        List<ItemInstance> items;
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
                itemList = List.of();
                return;
        }
        items.sort(ItemClassComparator.getInstance());
        itemList = items.stream()
                .map(ItemInfo::new)
                .collect(Collectors.toList());
    }

    public WareHouseWithdrawList(Clan clan, ItemClass clss) {
        adena = 0;
        type = WarehouseType.CLAN.ordinal();

        List<ItemInstance> items = clan == null ? new ArrayList<>() : clan.getWarehouse().getItems();

        items.sort(ItemClassComparator.getInstance());
        itemList = items.stream()
                .map(ItemInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    protected final void writeImpl() {
        writeC(0x42);
        writeH(type);
        writeQ(adena);
        writeH(itemList.size());
        itemList.forEach(item -> {
            writeItemInfo(item);
            writeD(item.getObjectId());
        });
    }
}