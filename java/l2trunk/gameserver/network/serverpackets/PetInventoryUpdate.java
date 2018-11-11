package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.ArrayList;
import java.util.List;


public class PetInventoryUpdate extends L2GameServerPacket {
    public static final int UNCHANGED = 0;
    private static final int ADDED = 1;
    private static final int MODIFIED = 2;
    private static final int REMOVED = 3;

    private final List<ItemInfo> _items = new ArrayList<>(1);

    public PetInventoryUpdate() {
    }

    public PetInventoryUpdate addNewItem(ItemInstance item) {
        addItem(item).setLastChange(ADDED);
        return this;
    }

    public PetInventoryUpdate addModifiedItem(ItemInstance item) {
        addItem(item).setLastChange(MODIFIED);
        return this;
    }

    public PetInventoryUpdate addRemovedItem(ItemInstance item) {
        addItem(item).setLastChange(REMOVED);
        return this;
    }

    private ItemInfo addItem(ItemInstance item) {
        ItemInfo info;
        _items.add(info = new ItemInfo(item));
        return info;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb4);
        writeH(_items.size());
        for (ItemInfo temp : _items) {
            writeH(temp.getLastChange());
            writeItemInfo(temp);
        }
    }
}