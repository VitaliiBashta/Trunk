package l2trunk.gameserver.model;

public final class PremiumItem {
    private final int itemId;
    private final String sender;
    private long count;

    public PremiumItem(int itemid, long count, String sender) {
        itemId = itemid;
        this.count = count;
        this.sender = sender;
    }

    public void updateCount(long newcount) {
        count = newcount;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public String getSender() {
        return sender;
    }
}