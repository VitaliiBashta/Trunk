package l2trunk.gameserver.model.items;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;

import java.util.stream.Collectors;

public final class AuctionStorage extends ItemContainer {
    private static AuctionStorage instance;

    private AuctionStorage() {
        restore();
    }

    public static AuctionStorage getInstance() {
        if (instance == null)
            instance = new AuctionStorage();
        return instance;
    }

    @Override
    public ItemInstance addItem(ItemInstance item, String owner, String log) {
        if (item == null)
            return null;

        if (item.getCount() < 1)
            return null;

        ItemInstance result;

        writeLock();
        try {
            getItems().add(item);
            result = item;
            item.setJdbcState(JdbcEntityState.CREATED);
            onAddItem(result);
        } finally {
            writeUnlock();
        }

        return result;
    }

    public void addFullItem(ItemInstance item) {
        if (item == null)
            return;

        if (item.getCount() < 1)
            return;

        ItemInstance result;

        writeLock();
        try {
            items.add(item);
            result = item;
            item.setJdbcState(JdbcEntityState.UPDATED);
            onAddItem(result);
        } finally {
            writeUnlock();
        }

    }

    @Override
    protected void onAddItem(ItemInstance item) {
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

    private ItemLocation getItemLocation() {
        return ItemLocation.AUCTION;
    }

    private void restore() {
        writeLock();
        try {
            getItems().clear();
            getItems().addAll(ITEMS_DAO.getItemsByLocation(ItemLocation.AUCTION).collect(Collectors.toList()));

        } finally {
            writeUnlock();
        }
    }
}