package l2trunk.gameserver.model.entity.CCPHelpers.itemLogs;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemLogList {
    private final Map<Integer, List<ItemActionLog>> logLists;

    private ItemLogList() {
        this.logLists = new ConcurrentHashMap<>();
    }

    public static ItemLogList getInstance() {
        return ItemLogListHolder.instance;
    }

    List<ItemActionLog> getLogs(Player player) {
        if (!Config.ENABLE_PLAYER_ITEM_LOGS) {
            return new ArrayList<>();
        }
        List<ItemActionLog> list = logLists.get(player.objectId());
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    void addLogs(ItemActionLog logs) {
        if (!Config.ENABLE_PLAYER_ITEM_LOGS) {
            return;
        }
        Integer playerObjectId = logs.getPlayerObjectId();
        List<ItemActionLog> list;
        if (this.logLists.containsKey(playerObjectId)) {
            list = this.logLists.get(playerObjectId);
        } else {
            list = new CopyOnWriteArrayList<>();
            this.logLists.put(playerObjectId, list);
        }

        list.add(logs);
    }

    private static class ItemLogListHolder {
        private static final ItemLogList instance = new ItemLogList();
    }
}