package l2trunk.gameserver.model.items;

public final class TradeItem extends ItemInfo {
    private long price;
    private long referencePrice;
    private long currentValue;
    private int lastRechargeTime;
    private int rechargeTime;
    private int auctionId;

    public TradeItem() {
//        super();
    }

    public TradeItem(ItemInstance item) {
//        super(item);
        setReferencePrice(item.getReferencePrice());
    }

    public long getOwnersPrice() {
        return price;
    }

    public void setOwnersPrice(long price) {
        this.price = price;
    }

    public long getReferencePrice() {
        return referencePrice;
    }

    private void setReferencePrice(long price) {
        referencePrice = price;
    }

    public long getStorePrice() {
        return getReferencePrice() / 2;
    }

    public long getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(long value) {
        currentValue = value;
    }

    /**
     * Возвращает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @return unixtime в минутах
     */
    public int getRechargeTime() {
        return rechargeTime;
    }

    /**
     * Устанавливает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @param rechargeTime : unixtime в минутах
     */
    public void setRechargeTime(int rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    /**
     * Возвращает ограничен ли этот предмет в количестве, используется в NPC магазинах с ограниченным количеством.
     *
     * @return true, если ограничен
     */
    public boolean isCountLimited() {
        return getCount() > 0;
    }

    /**
     * Возвращает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @return unixtime в минутах
     */
    public int getLastRechargeTime() {
        return lastRechargeTime;
    }

    /**
     * Устанавливает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @param lastRechargeTime : unixtime в минутах
     */
    public void setLastRechargeTime(int lastRechargeTime) {
        this.lastRechargeTime = lastRechargeTime;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int id) {
        auctionId = id;
    }
}