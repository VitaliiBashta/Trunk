package l2trunk.gameserver.model.reward;

import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.ItemFunctions;

public final class RewardItemResult {
    private final int itemId;
    private long count;

    public RewardItemResult(int itemId, long count) {
        this.itemId = itemId;
        this.count = count;
    }

    public ItemInstance createItem() {
        if (count < 1)
            return null;

        ItemInstance item = ItemFunctions.createItem(itemId);
        if (item != null) {
            item.setCount(count);
            return item;
        }

        return null;
    }
}
