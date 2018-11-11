package l2trunk.gameserver.model.reward;

import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.ItemFunctions;

public class RewardItemResult {
    private final int _itemId;
    private long _count;
    private boolean _isAdena;

    public RewardItemResult(int itemId) {
        _itemId = itemId;
        _count = 1;
    }

    public RewardItemResult(int itemId, long count) {
        _itemId = itemId;
        _count = count;
    }

    public int getItemId() {
        return _itemId;
    }

    public long getCount() {
        return _count;
    }

    public RewardItemResult setCount(long count) {
        _count = count;
        return this;
    }

    public boolean isAdena() {
        return _isAdena;
    }

    public void setIsAdena(boolean val) {
        _isAdena = val;
    }

    public ItemInstance createItem() {
        if (_count < 1)
            return null;

        ItemInstance item = ItemFunctions.createItem(_itemId);
        if (item != null) {
            item.setCount(_count);
            return item;
        }

        return null;
    }
}
