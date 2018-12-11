package l2trunk.gameserver.model.items;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.ItemTemplate.ItemClass;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Warehouse extends ItemContainer {
    private static final ItemsDAO _itemsDAO = ItemsDAO.INSTANCE;
    private final int _ownerId;

    Warehouse(int ownerId) {
        _ownerId = ownerId;
    }

    private int getOwnerId() {
        return _ownerId;
    }

    protected abstract ItemLocation getItemLocation();

    public synchronized List<ItemInstance> getItems(ItemClass itemClass) {
        return items.stream()
                .filter(item -> (itemClass == null || itemClass == ItemClass.ALL) || item.getItemClass() == itemClass)
                .collect(Collectors.toList());
    }

    public long getCountOfAdena() {
        return getCountOf(ItemTemplate.ITEM_ID_ADENA);
    }

    @Override
    protected void onAddItem(ItemInstance item) {
        item.setOwnerId(getOwnerId());
        item.setLocation(getItemLocation());
        item.setLocData(0);
        if (item.getJdbcState().isSavable()) {
            item.save();
        } else {
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();
        }
    }

    @Override
    protected void onModifyItem(ItemInstance item) {
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
    }

    @Override
    protected void onRemoveItem(ItemInstance item) {
        item.setLocData(-1);
    }

    @Override
    protected void onDestroyItem(ItemInstance item) {
        item.setCount(0L);
        item.delete();
    }

    public void restore() {
        writeLock();
        try {
            Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(getOwnerId(), getItemLocation());

            this.items.addAll(items);
        } finally {
            writeUnlock();
        }
    }

    public enum WarehouseType {
        NONE,
        PRIVATE,
        CLAN,
        CASTLE,
        FREIGHT
    }

    public static class ItemClassComparator implements Comparator<ItemInstance>, Serializable {
        private static final Comparator<ItemInstance> instance = new ItemClassComparator();

        public static Comparator<ItemInstance> getInstance() {
            return instance;
        }

        @Override
        public int compare(ItemInstance o1, ItemInstance o2) {
            if (o1 == null || o2 == null)
                return 0;
            int diff = Integer.compare(o1.getItemClass().ordinal(), o2.getItemClass().ordinal());
            if (diff == 0)
                diff = Integer.compare(o1.getCrystalType().ordinal(), o2.getCrystalType().ordinal());
            if (diff == 0)
                diff = Integer.compare(o1.getItemId(), o2.getItemId());
            if (diff == 0)
                diff = Integer.compare(o1.getEnchantLevel(), o2.getEnchantLevel());
            return diff;
        }
    }
}