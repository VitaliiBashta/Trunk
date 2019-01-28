package l2trunk.gameserver.model.entity.CCPHelpers.itemLogs;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.HtmPropHolder;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class CCPItemLogs {

    private static int tableHeight = -100;
    private static int headerHeight = -100;
    private static int itemHeight = -100;
    private static int maxHeight = -100;

    public static void showPage(Player player) {
        showPage(player, 0);
    }

    public static void showPage(Player player, int pageIndex) {
        if (!Config.ENABLE_PLAYER_ITEM_LOGS) {
            return;
        }
        if (tableHeight == -100) {
            Map<String, String> props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
            tableHeight = Integer.parseInt(props.get("table_height"));
            headerHeight = Integer.parseInt(props.get("header_height"));
            itemHeight = Integer.parseInt(props.get("item_height"));
            maxHeight = Integer.parseInt(props.get("page_max_height"));
        }

        String html = preparePage(player, pageIndex);
        ShowBoard.separateAndSend(html, player);
    }

    private static String preparePage(Player player, int pageIndex) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Map<String, String> props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/itemLogs.htm", player);

        List<ItemActionLog> allLogs = ItemLogList.getInstance().getLogs(player);
        Collections.reverse(ItemLogList.getInstance().getLogs(player));
        int[] pageItemToStartFrom = getLogIndexToStartFrom(allLogs, pageIndex);

        StringBuilder tablesLeft = new StringBuilder();
        StringBuilder tablesRight = new StringBuilder();
        int side = 0;
        int heightReached = 0;
        int startingItemIndex = pageItemToStartFrom[1];
        int itemIndex = 0;
        int logCount = startingItemIndex;

        for (int logIndex = pageItemToStartFrom[0]; (logIndex < allLogs.size()) && (side < 2); logIndex++) {
            boolean changeSide = false;
            ItemActionLog log = allLogs.get(logIndex);

            String table = getLogsTable(player, log, heightReached, startingItemIndex, itemIndex, dateFormat);
            if ((table == null) || (player.containsQuickVar("CCPItemLogsStartingItemIndex"))) {
                changeSide = true;
                heightReached = 0;
                logCount--;
                startingItemIndex = player.getQuickVarI("CCPItemLogsStartingItemIndex", 0);
                player.deleteQuickVar("CCPItemLogsStartingItemIndex");
            } else {
                itemIndex += log.getItemsReceived().length + log.getItemsLost().length;
                heightReached += player.getQuickVarI("CCPItemLogsHeightReached", 0);
                startingItemIndex = 0;
                logCount++;
            }

            if (table != null) {
                if (side == 0)
                    tablesLeft.append(table);
                else {
                    tablesRight.append(table);
                }
            }
            if (changeSide) {
                side++;
            }
        }
        html = html.replace("%tablesLeft%", tablesLeft.length() > 0 ? tablesLeft : "<br>");
        html = html.replace("%tablesRight%", tablesRight.length() > 0 ? tablesRight : "<br>");
        html = html.replace("%previousBtn%", pageIndex > 0 ? props.get("PreviousBtn").replace("%page%", String.valueOf(pageIndex - 1)) : "<br>");
        html = html.replace("%nextBtn%", (logCount < allLogs.size()) || (startingItemIndex > 0) ? props.get("NextBtn").replace("%page%", String.valueOf(pageIndex + 1)) : "<br>");

        return html;
    }

    private static String getLogsTable(Player player, ItemActionLog log, int heightReached, int startingItemIndex, int itemIndex, SimpleDateFormat dateFormat) {
        Map<String, String> props = HtmPropHolder.getList(Config.BBS_HOME_DIR + "pages/itemLogs.prop.htm");
        String date = dateFormat.format(new Date(log.getTime()));

        int newHeight = heightReached;
        if (newHeight + tableHeight + headerHeight + itemHeight > maxHeight) {
            return null;
        }

        newHeight += tableHeight;
        String table = props.get("table");

        if (startingItemIndex == 0) {
            String header = props.get("header");
            header = header.replace("%actionType%", log.getActionType().getNiceName());
            table = table.replace("%header%", header);
            newHeight += headerHeight;
        } else {
            table = table.replace("%header%", "");
        }

        StringBuilder itemsBuilder = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            SingleItemLog[] items = i == 0 ? log.getItemsReceived() : log.getItemsLost();
            if (startingItemIndex > items.length) {
                startingItemIndex -= items.length;
            } else {
                for (int currentItemIndex = startingItemIndex; currentItemIndex < items.length; currentItemIndex++) {
                    SingleItemLog item = items[currentItemIndex];

                    if (newHeight + itemHeight > maxHeight) {
                        int totalItemIndex = currentItemIndex + (i > 0 ? log.getItemsReceived().length : 0);
                        player.addQuickVar("CCPItemLogsStartingItemIndex", totalItemIndex);
                        return table.replace("%items%", itemsBuilder.toString());
                    }

                    ItemTemplate template = ItemHolder.getTemplate(item.getItemTemplateId());
                    String itemText = props.get("item");
                    itemText = itemText.replace("%itemTableColor%", itemIndex % 2 == 0 ? props.get("item_table_color_0") : props.get("item_table_color_1"));
                    itemText = itemText.replace("%icon%", template.getIcon());
                    String itemName = template.getName() + (item.getItemEnchantLevel() > 0 ? " + " + item.getItemEnchantLevel() : "") + (item.getItemCount() > 1L ? new StringBuilder().append(" x ").append(item.getItemCount()).toString() : "");
                    itemText = itemText.replace("%itemName%", itemName);
                    itemText = itemText.replace("%time%", date);
                    String receiverName = (item.getReceiverName() != null) && (!item.getReceiverName().isEmpty()) ? item.getReceiverName() : "Nobody";
                    itemText = itemText.replace("%receiverColor%", receiverName.equals(player.getName()) ? props.get("receiver_color_owner") : props.get("receiver_color_alien"));
                    itemText = itemText.replace("%receiverName%", receiverName);

                    itemsBuilder.append(itemText);
                    itemIndex++;
                    newHeight += itemHeight;
                }

                startingItemIndex = 0;
            }
        }

        player.deleteQuickVar("CCPItemLogsStartingItemIndex");
        player.addQuickVar("CCPItemLogsHeightReached", newHeight - heightReached);

        return table.replace("%items%", itemsBuilder.toString());
    }

    private static int[] getLogIndexToStartFrom(List<ItemActionLog> allLogs, int pageIndexToReach) {
        if (pageIndexToReach <= 0) {
            return new int[]
                    {
                            0,
                            0
                    };
        }
        int pageReached = 0;
        boolean useRightSide = false;
        int heightReached = 0;
        int startingItem = 0;

        for (int logIndex = 0; logIndex < allLogs.size(); logIndex++) {
            ItemActionLog log = allLogs.get(logIndex);

            int[] itemHeightReached = getItemAndHeightReached(log, startingItem, heightReached);

            startingItem = itemHeightReached[0];
            heightReached = itemHeightReached[1];
            if ((startingItem < Integer.MAX_VALUE)) {
                heightReached = 0;

                if (startingItem < 0) {
                    startingItem = 0;
                }
                if (useRightSide) {
                    pageReached++;
                    if (pageReached >= pageIndexToReach) {
                        return new int[]
                                {
                                        logIndex,
                                        startingItem
                                };
                    }
                } else {
                    useRightSide = true;
                }
                logIndex--;
            } else {
                startingItem = 0;
            }
        }
        return new int[]
                {
                        0,
                        0
                };
    }

    private static int[] getItemAndHeightReached(ItemActionLog log, int startFromItem, int heightReached) {
        int newHeight = heightReached;

        if (newHeight + tableHeight + headerHeight + itemHeight > maxHeight) {
            return new int[]
                    {
                            -1,
                            newHeight
                    };
        }

        newHeight += tableHeight;
        if (startFromItem == 0) {
            newHeight += headerHeight;
        }
        for (int item = startFromItem; item < log.getItemsReceived().length + log.getItemsLost().length; item++) {
            if (newHeight + itemHeight > maxHeight) {
                return new int[]
                        {
                                item,
                                newHeight
                        };
            }
            newHeight += itemHeight;
        }
        return new int[]
                {
                        Integer.MAX_VALUE,
                        newHeight
                };
    }
}