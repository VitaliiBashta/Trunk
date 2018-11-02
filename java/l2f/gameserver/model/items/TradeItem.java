package l2f.gameserver.model.items;

public final class TradeItem extends ItemInfo {
    private long _price;
    private long _referencePrice;
    private long _currentValue;
    private int _lastRechargeTime;
    private int _rechargeTime;
    private int _auctionId;

    public TradeItem() {
        super();
    }

    public TradeItem(ItemInstance item) {
        super(item);
        setReferencePrice(item.getReferencePrice());
    }

    public long getOwnersPrice() {
        return _price;
    }

    public void setOwnersPrice(long price) {
        _price = price;
    }

    public long getReferencePrice() {
        return _referencePrice;
    }

    public void setReferencePrice(long price) {
        _referencePrice = price;
    }

    public long getStorePrice() {
        return getReferencePrice() / 2;
    }

    public long getCurrentValue() {
        return _currentValue;
    }

    public void setCurrentValue(long value) {
        _currentValue = value;
    }

    /**
     * Возвращает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @return unixtime в минутах
     */
    public int getRechargeTime() {
        return _rechargeTime;
    }

    /**
     * Устанавливает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @param rechargeTime : unixtime в минутах
     */
    public void setRechargeTime(int rechargeTime) {
        _rechargeTime = rechargeTime;
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
        return _lastRechargeTime;
    }

    /**
     * Устанавливает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
     *
     * @param lastRechargeTime : unixtime в минутах
     */
    public void setLastRechargeTime(int lastRechargeTime) {
        _lastRechargeTime = lastRechargeTime;
    }

    public int getAuctionId() {
        return _auctionId;
    }

    public void setAuctionId(int id) {
        _auctionId = id;
    }
}