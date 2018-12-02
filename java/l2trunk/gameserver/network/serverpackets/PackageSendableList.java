package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.Warehouse;

import java.util.ArrayList;
import java.util.List;

public final class PackageSendableList extends L2GameServerPacket {
    private final int targetObjectId;
    private final long adena;
    private final List<ItemInfo> _itemList;

    public PackageSendableList(int objectId, Player cha) {
        adena = cha.getAdena();
        targetObjectId = objectId;

        List<ItemInstance> items = cha.getInventory().getItems();
        items.sort(Warehouse.ItemClassComparator.getInstance());
        _itemList = new ArrayList<>(items.size());
        items.stream()
                .filter(item -> item.getTemplate().isFreightable())
                .forEach(item -> _itemList.add(new ItemInfo(item)));
    }

    @Override
    protected final void writeImpl() {
        writeC(0xD2);
        writeD(targetObjectId);
        writeQ(adena);
        writeD(_itemList.size());
        for (ItemInfo item : _itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}
