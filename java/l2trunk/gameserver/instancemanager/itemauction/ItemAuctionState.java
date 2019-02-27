package l2trunk.gameserver.instancemanager.itemauction;

import java.util.stream.Stream;

public enum ItemAuctionState {
    CREATED(0), STARTED(1), FINISHED(2);

    private int id;

    ItemAuctionState(int id) {
        this.id = id;
    }

    public static ItemAuctionState of(int id) {
        return Stream.of(values()).filter(v -> v.id == id).findFirst().orElse(null);

    }

}