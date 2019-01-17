package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.model.pledge.Clan;

public final class AuctionSiegeClanObject extends SiegeClanObject {
    private long bid;

    public AuctionSiegeClanObject(String type, Clan clan, long param) {
        this(type, clan, param, System.currentTimeMillis());
    }

    public AuctionSiegeClanObject(String type, Clan clan, long param, long date) {
        super(type, clan, param, date);
        bid = param;
    }

    @Override
    public long getParam() {
        return bid;
    }

    public void setParam(long param) {
        bid = param;
    }
}
