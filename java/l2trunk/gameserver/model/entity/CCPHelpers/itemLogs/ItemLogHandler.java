package l2trunk.gameserver.model.entity.CCPHelpers.itemLogs;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.TradeItem;

import java.util.List;

public enum ItemLogHandler {
    INSTANCE;
    private static final SingleItemLog[] EMPTY_RECEIVED_ITEMS = new SingleItemLog[0];
    private final Object lock;
    private int lastActionId;

    ItemLogHandler() {
        this.lastActionId = 0;
        this.lock = new Object();
    }

    public void addLog(Player player, ItemInstance itemLost, long count, ItemActionType actionType) {
        SingleItemLog[] lostItems = new SingleItemLog[1];
        lostItems[0] = new SingleItemLog(itemLost.getItemId(), count, itemLost.getEnchantLevel(), itemLost.objectId());
        addLog(player, lostItems, actionType);
    }

    public void addLog(Player player, List<ItemInstance> itemsLost, ItemActionType actionType) {
        SingleItemLog[] lostItems = new SingleItemLog[itemsLost.size()];
        for (int i = 0; i < itemsLost.size(); i++) {
            ItemInstance itemLost = itemsLost.get(i);
            lostItems[i] = new SingleItemLog(itemLost.getItemId(), itemLost.getCount(), itemLost.getEnchantLevel(), itemLost.objectId());
        }
        addLog(player, lostItems, actionType);
    }

    public void addLog(Player player, List<ItemInstance> itemsLost, String receiverName, ItemActionType actionType) {
        SingleItemLog[] lostItems = new SingleItemLog[itemsLost.size()];
        for (int i = 0; i < itemsLost.size(); i++) {
            ItemInstance itemLost = itemsLost.get(i);
            lostItems[i] = new SingleItemLog(itemLost.getItemId(), itemLost.getCount(), itemLost.getEnchantLevel(), itemLost.objectId());
            lostItems[i].setReceiverName(receiverName);
        }

        addLog(player, lostItems, actionType);
    }

    private SingleItemLog[] getAdenaItemLog(Player oldOwner, Player newOwner, long count) {
        SingleItemLog[] lostItems = new SingleItemLog[1];
        ItemInstance item = oldOwner.getInventory().getItemByItemId(57);
        lostItems[0] = new SingleItemLog(57, count, 0, item == null ? -1 : item.objectId());
        lostItems[0].setReceiverName(newOwner.getName());
        return lostItems;
    }

    private void addLogItemsForAdena(Player itemsWinner, Player adenaWinner, List<TradeItem> receivedItems, long lostAdenaCount, ItemActionType itemsWinnerActionType, ItemActionType adenaWinnerActionType) {
        SingleItemLog[] itemLogs = new SingleItemLog[receivedItems.size()];
        for (int i = 0; i < receivedItems.size(); i++) {
            TradeItem item = receivedItems.get(i);
            itemLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
            itemLogs[i].setReceiverName(itemsWinner.getName());
        }
        SingleItemLog[] adenaLogs = getAdenaItemLog(itemsWinner, adenaWinner, lostAdenaCount);

        addLog(itemsWinner, adenaLogs, itemLogs, itemsWinnerActionType);

        addLog(adenaWinner, itemLogs, adenaLogs, adenaWinnerActionType);
    }

    private void addLogItemForItems(Player buyer, Player seller, List<TradeItem> boughtItems, List<TradeItem> givenItems, ItemActionType actionType) {
        SingleItemLog[] boughtItemsLogs = new SingleItemLog[boughtItems.size()];
        for (int i = 0; i < boughtItems.size(); i++) {
            TradeItem item = boughtItems.get(i);
            boughtItemsLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
            boughtItemsLogs[i].setReceiverName(buyer.getName());
        }
        SingleItemLog[] givenItemsLogs = new SingleItemLog[givenItems.size()];
        for (int i = 0; i < givenItems.size(); i++) {
            TradeItem item = givenItems.get(i);
            givenItemsLogs[i] = new SingleItemLog(item.getItemId(), item.getCount(), item.getEnchantLevel(), item.getObjectId());
            givenItemsLogs[i].setReceiverName(seller.getName());
        }

        addLog(buyer, givenItemsLogs, boughtItemsLogs, actionType);
    }

    public void addLog(Player seller, Player buyer, List<TradeItem> sellList, long adenaReward, ItemActionType sellerActionType, ItemActionType buyerActionType) {
        addLogItemsForAdena(buyer, seller, sellList, adenaReward, buyerActionType, sellerActionType);
    }

    public void addLog(Player trader1, Player trader2, List<TradeItem> itemsFromTrader1, List<TradeItem> itemsFromTrader2, ItemActionType actionType) {
        addLogItemForItems(trader2, trader1, itemsFromTrader2, itemsFromTrader1, actionType);
        addLogItemForItems(trader1, trader2, itemsFromTrader1, itemsFromTrader2, actionType);
    }

    private void addLog(Player player, SingleItemLog[] lostItems, ItemActionType actionType) {
        addLog(player, lostItems, EMPTY_RECEIVED_ITEMS, actionType);
    }

    private void addLog(Player player, SingleItemLog[] lostItems, SingleItemLog[] receivedItems, ItemActionType actionType) {
        long time = System.currentTimeMillis();

        ItemActionLog actionLog = new ItemActionLog(getNextActionId(), player.objectId(), actionType, time, lostItems, receivedItems);

        ItemLogList.getInstance().addLogs(actionLog);
    }

    private int getNextActionId() {
        int actionId;
        synchronized (this.lock) {
            this.lastActionId += 1;
            actionId = this.lastActionId;
        }
        return actionId;
    }

}