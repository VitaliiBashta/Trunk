package l2trunk.gameserver.instancemanager.itemauction;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.ItemFunctions;

public final class AuctionItem extends ItemInfo {
    private final int auctionItemId;
    private final int auctionLength;
    private final long auctionInitBid;

    public AuctionItem(final int auctionItemId, final int auctionLength, final long auctionInitBid,
                       final int itemId, final long itemCount, boolean altByItem,
                       final StatsSet itemExtra) {
        this.auctionItemId = auctionItemId;
        this.auctionLength = auctionLength;
        this.auctionInitBid = auctionInitBid;

        setObjectId(itemId);
        setItemId(itemId);
        setCount(itemCount);
        setEquipped(altByItem);
        setEnchantLevel(itemExtra.getInteger("enchant_level", 0));
        setAugmentationId(itemExtra.getInteger("augmentation_id", 0));
    }

    public final int getAuctionItemId() {
        return auctionItemId;
    }

    public final int getAuctionLength() {
        return auctionLength;
    }

    public final long getAuctionInitBid() {
        return auctionInitBid;
    }

    public final ItemInstance createNewItemInstance() {
        final ItemInstance item = ItemFunctions.createItem(getItemId());
        item.setEnchantLevel(getEnchantLevel());
        if (getAugmentationId() != 0)
            item.setAugmentationId(getAugmentationId());

        return item;
    }
}