package l2trunk.gameserver.instancemanager.itemauction;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;

public final class ItemAuctionBid {
    private final int charId;
    private long lastBid;

    ItemAuctionBid(int charId, long lastBid) {
        this.charId = charId;
        this.lastBid = lastBid;
    }

    public final int getCharId() {
        return charId;
    }

    public final long getLastBid() {
        return lastBid;
    }

    final void setLastBid(long lastBid) {
        this.lastBid = lastBid;
    }

    final void cancelBid() {
        lastBid = -1;
    }

    final boolean isCanceled() {
        return lastBid == -1;
    }

    final Player getPlayer() {
        return GameObjectsStorage.getPlayer(charId);
    }
}