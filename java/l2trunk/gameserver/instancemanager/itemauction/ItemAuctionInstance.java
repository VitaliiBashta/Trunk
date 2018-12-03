package l2trunk.gameserver.instancemanager.itemauction;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ItemAuctionInstance {
    private static final Logger _log = LoggerFactory.getLogger(ItemAuctionInstance.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static final long START_TIME_SPACE = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    private static final long FINISH_TIME_SPACE = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

    private final int _instanceId;
    private final Map<Integer, ItemAuction> _auctions;
    private final List<AuctionItem> _items;
    private final SchedulingPattern _dateTime;

    private ItemAuction currentAuction;
    private ItemAuction nextAuction;
    private ScheduledFuture<?> _stateTask;

    ItemAuctionInstance(int instanceId, SchedulingPattern dateTime, List<AuctionItem> items) {
        _instanceId = instanceId;
        _auctions = new HashMap<>();
        _items = items;
        _dateTime = dateTime;

        load();

        _log.info("ItemAuction: Loaded " + _items.size() + " item(s) and registered " + _auctions.size() + " auction(s) for instance " + _instanceId + ".");
        checkAndSetCurrentAndNextAuction();
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT auctionId FROM item_auction WHERE instanceId=?")) {
            statement.setInt(1, _instanceId);
            ResultSet rset = statement.executeQuery();

            while (rset.next()) {
                int auctionId = rset.getInt(1);
                try {
                    ItemAuction auction = loadAuction(auctionId);
                    if (auction != null) {
                        _auctions.put(auctionId, auction);
                    } else {
                        ItemAuctionManager.INSTANCE.deleteAuction(auctionId);
                    }
                } catch (SQLException e) {
                    _log.warn("ItemAuction: Failed loading auction: " + auctionId, e);
                }
            }
        } catch (SQLException e) {
            _log.error("ItemAuction: Failed loading auctions.", e);
        }
    }

    public ItemAuction getCurrentAuction() {
        return currentAuction;
    }

    public ItemAuction getNextAuction() {
        return nextAuction;
    }

    public void shutdown() {
        ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
    }

    private AuctionItem getAuctionItem(int auctionItemId) {
        for (int i = _items.size(); i-- > 0; ) {
            try {
                AuctionItem item = _items.get(i);
                if (item.getAuctionItemId() == auctionItemId) {
                    return item;
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void checkAndSetCurrentAndNextAuction() {
        List<ItemAuction> auctions = new ArrayList<>(_auctions.values());

        ItemAuction currentAuction = null;
        ItemAuction nextAuction = null;

        switch (auctions.size()) {
            case 0: {
                nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                break;
            }

            case 1: {
                switch (auctions.get(0).getAuctionState()) {
                    case CREATED: {
                        if (auctions.get(0).getStartingTime() < (System.currentTimeMillis() + START_TIME_SPACE)) {
                            currentAuction = auctions.get(0);
                            nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        } else {
                            nextAuction = auctions.get(0);
                        }
                        break;
                    }

                    case STARTED: {
                        currentAuction = auctions.get(0);
                        nextAuction = createAuction(Math.max(currentAuction.getEndingTime() + FINISH_TIME_SPACE, System.currentTimeMillis() + START_TIME_SPACE));
                        break;
                    }

                    case FINISHED: {
                        currentAuction = auctions.get(0);
                        nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        break;
                    }

                    default:
                        throw new IllegalArgumentException();
                }
                break;
            }

            default: {
                auctions.sort((o1, o2) -> Long.compare(o2.getStartingTime(), o1.getStartingTime()));

                // just to make sure we won`t skip any auction because of little different times
                long currentTime = System.currentTimeMillis();

                for (ItemAuction auction : auctions) {
                    if (auction.getAuctionState() == ItemAuctionState.STARTED) {
                        currentAuction = auction;
                        break;
                    } else if (auction.getStartingTime() <= currentTime) {
                        currentAuction = auction;
                        break;
                    }
                }

                for (ItemAuction auction : auctions) {
                    if ((auction.getStartingTime() > currentTime) && (currentAuction != auction)) {
                        nextAuction = auction;
                        break;
                    }
                }

                if (nextAuction == null) {
                    nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                }
                break;
            }
        }

        _auctions.put(nextAuction.getAuctionId(), nextAuction);

        this.currentAuction = currentAuction;
        this.nextAuction = nextAuction;

        if ((currentAuction != null) && (currentAuction.getAuctionState() == ItemAuctionState.STARTED)) {
            setStateTask(ThreadPoolManager.INSTANCE.schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getEndingTime() - System.currentTimeMillis(), 0L)));
            _log.info("ItemAuction: Schedule current auction " + currentAuction.getAuctionId() + " for instance " + _instanceId);
        } else {
            setStateTask(ThreadPoolManager.INSTANCE.schedule(new ScheduleAuctionTask(nextAuction), Math.max(nextAuction.getStartingTime() - System.currentTimeMillis(), 0L)));
            _log.info("ItemAuction: Schedule next auction " + nextAuction.getAuctionId() + " on " + DATE_FORMAT.format(new Date(nextAuction.getStartingTime())) + " for instance " + _instanceId);
        }
    }

    public ItemAuction getAuction(int auctionId) {
        return _auctions.get(auctionId);
    }

    public List<ItemAuction> getAuctionsByBidder(int bidderObjId) {
        Collection<ItemAuction> auctions = getAuctions();
        List<ItemAuction> stack = new ArrayList<>(auctions.size());
        for (ItemAuction auction : getAuctions()) {
            if (auction.getAuctionState() != ItemAuctionState.CREATED) {
                ItemAuctionBid bid = auction.getBidFor(bidderObjId);
                if (bid != null) {
                    stack.add(auction);
                }
            }
        }
        return stack;
    }

    private Collection<ItemAuction> getAuctions() {
        synchronized (_auctions) {
            return _auctions.values();
        }
    }

    private void onAuctionFinished(ItemAuction auction) {
        auction.broadcastToAllBidders(new SystemMessage2(SystemMsg.S1S_AUCTION_HAS_ENDED).addInteger(auction.getAuctionId()));
        ItemAuctionBid bid = auction.getHighestBid();
        if (bid != null) {
            ItemInstance item = auction.createNewItemInstance();
            Player player = bid.getPlayer();
            if (player != null) {
                player.getWarehouse().addItem(item, "WH" + player.toString(), "ItemAuctionFinish");
                player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL));

                _log.info("ItemAuction: Auction " + auction.getAuctionId() + " has finished. Highest bid by (name) " + player.getName() + " for instance " + _instanceId);
            } else {
                // TODO [G1ta0] send mail
                item.setOwnerId(bid.getCharId());
                item.setLocation(ItemLocation.WAREHOUSE);
                item.setJdbcState(JdbcEntityState.UPDATED);
                item.update();

                _log.info("ItemAuction: Auction " + auction.getAuctionId() + " has finished. Highest bid by (id) " + bid.getCharId() + " for instance " + _instanceId);
            }
        } else {
            _log.info("ItemAuction: Auction " + auction.getAuctionId() + " has finished. There have not been any bid for instance " + _instanceId);
        }
    }

    private void setStateTask(ScheduledFuture<?> future) {
        ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
        _stateTask = future;
    }

    private ItemAuction createAuction(long after) {
        AuctionItem auctionItem = _items.get(Rnd.get(_items.size()));
        long startingTime = _dateTime.next(after);
        long endingTime = startingTime + TimeUnit.MILLISECONDS.convert(auctionItem.getAuctionLength(), TimeUnit.MINUTES);
        int auctionId = ItemAuctionManager.INSTANCE.getNextId();
        ItemAuction auction = new ItemAuction(auctionId, _instanceId, startingTime, endingTime, auctionItem, ItemAuctionState.CREATED);

        auction.store();

        return auction;
    }

    private ItemAuction loadAuction(int auctionId) throws SQLException {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("SELECT auctionItemId,startingTime,endingTime,auctionStateId FROM item_auction WHERE auctionId=?");
            statement.setInt(1, auctionId);
            ResultSet rset = statement.executeQuery();

            if (!rset.next()) {
                _log.warn("ItemAuction: Auction data not found for auction: " + auctionId);
                return null;
            }

            int auctionItemId = rset.getInt(1);
            long startingTime = rset.getLong(2);
            long endingTime = rset.getLong(3);
            int auctionStateId = rset.getInt(4);

            if (startingTime >= endingTime) {
                _log.warn("ItemAuction: Invalid starting/ending paramaters for auction: " + auctionId);
                return null;
            }

            AuctionItem auctionItem = getAuctionItem(auctionItemId);
            if (auctionItem == null) {
                _log.warn("ItemAuction: AuctionItem: " + auctionItemId + ", not found for auction: " + auctionId);
                return null;
            }

            ItemAuctionState auctionState = ItemAuctionState.stateForStateId(auctionStateId);
            if (auctionState == null) {
                _log.warn("ItemAuction: Invalid auctionStateId: " + auctionStateId + ", for auction: " + auctionId);
                return null;
            }

            ItemAuction auction = new ItemAuction(auctionId, _instanceId, startingTime, endingTime, auctionItem, auctionState);

            statement = con.prepareStatement("SELECT playerObjId,playerBid FROM item_auction_bid WHERE auctionId=?");
            statement.setInt(1, auctionId);
            rset = statement.executeQuery();

            while (rset.next()) {
                int charId = rset.getInt(1);
                long playerBid = rset.getLong(2);
                ItemAuctionBid bid = new ItemAuctionBid(charId, playerBid);
                auction.addBid(bid);
            }

            return auction;
        }
    }

    private class ScheduleAuctionTask extends RunnableImpl {
        private final ItemAuction auction;

        ScheduleAuctionTask(ItemAuction auction) {
            this.auction = auction;
        }

        @Override
        public void runImpl() {
            ItemAuctionState state = auction.getAuctionState();

            switch (state) {
                case CREATED: {
                    if (!auction.setAuctionState(state, ItemAuctionState.STARTED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.STARTED.toString() + ", expected: " + state.toString());
                    }

                    _log.info("ItemAuction: Auction " + auction.getAuctionId() + " has started for instance " + auction.getInstanceId());
                    if (Config.ALT_ITEM_AUCTION_START_ANNOUNCE) {
                        String[] params = {};
                        Announcements.INSTANCE.announceByCustomMessage("l2trunk.gameserver.model.instances.L2ItemAuctionBrokerInstance.announce." + auction.getInstanceId(), params);
                    }
                    checkAndSetCurrentAndNextAuction();
                    break;
                }

                case STARTED: {
                    switch (auction.getAuctionEndingExtendState()) {
                        case 1: {
                            if (auction.getScheduledAuctionEndingExtendState() == 0) {
                                auction.setScheduledAuctionEndingExtendState(1);
                                setStateTask(ThreadPoolManager.INSTANCE().schedule(this, Math.max(auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }

                        case 2: {
                            if (auction.getScheduledAuctionEndingExtendState() != 2) {
                                auction.setScheduledAuctionEndingExtendState(2);
                                setStateTask(ThreadPoolManager.INSTANCE.schedule(this, Math.max(auction.getEndingTime() - System.currentTimeMillis(), 0L)));
                                return;
                            }
                            break;
                        }
                    }

                    if (!auction.setAuctionState(state, ItemAuctionState.FINISHED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.FINISHED.toString() + ", expected: " + state.toString());
                    }

                    onAuctionFinished(auction);
                    checkAndSetCurrentAndNextAuction();
                    break;
                }

                default:
                    throw new IllegalStateException("Invalid state: " + state);
            }
        }
    }
}