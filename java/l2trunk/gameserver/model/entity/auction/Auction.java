package l2trunk.gameserver.model.entity.auction;

import l2trunk.gameserver.model.items.ItemInstance;

public final class Auction {
    private final int auctionId;
    private final int sellerObjectId;
    private final String sellerName;
    private final ItemInstance item;
    private final long pricePerItem;
    private final AuctionItemTypes itemType;
    private final boolean privateStore;
    private long countToSell;

    public Auction(int id, int sellerObjectId, String sellerName, ItemInstance item, long pricePerItem, long countToSell, AuctionItemTypes itemType, boolean privateStore) {
        auctionId = id;
        this.sellerObjectId = sellerObjectId;
        this.sellerName = sellerName;
        this.item = item;
        this.pricePerItem = pricePerItem;
        this.countToSell = countToSell;
        this.itemType = itemType;
        this.privateStore = privateStore;
    }

    public int auctionId() {
        return auctionId;
    }

    public int getSellerObjectId() {
        return sellerObjectId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public ItemInstance getItem() {
        return item;
    }

    public void setCount(long count) {
        countToSell = count;
    }

    public long getCountToSell() {
        return countToSell;
    }

    public long getPricePerItem() {
        return pricePerItem;
    }

    public AuctionItemTypes getItemType() {
        return itemType;
    }

    public boolean isPrivateStore() {
        return privateStore;
    }
}
