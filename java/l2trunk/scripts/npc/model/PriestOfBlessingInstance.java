package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class PriestOfBlessingInstance extends NpcInstance {
    private static final List<Hourglass> hourglassList = new ArrayList<>();

    static {
        hourglassList.add(new Hourglass(1, 19, 4000, List.of(17095, 17096, 17097, 17098, 17099))); // 1-19
        hourglassList.add(new Hourglass(20, 39, 30000, List.of(17100, 17101, 17102, 17103, 17104))); // 20-39
        hourglassList.add(new Hourglass(40, 51, 110000, List.of(17105, 17106, 17107, 17108, 17109))); // 40-51
        hourglassList.add(new Hourglass(52, 60, 310000, List.of(17110, 17111, 17112, 17113, 17114))); // 52-60
        hourglassList.add(new Hourglass(61, 75, 970000, List.of(17115, 17116, 17117, 17118, 17119))); // 61-75
        hourglassList.add(new Hourglass(76, 79, 2160000, List.of(17120, 17121, 17122, 17123, 17124))); // 76-79
        hourglassList.add(new Hourglass(80, 85, 5000000, List.of(17125, 17126, 17127, 17128, 17129))); // 80-85
    }

    public PriestOfBlessingInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private static Hourglass getHourglass(Player player) {
        return hourglassList.stream()
                .filter(hg -> player.getLevel() >= hg.minLevel)
                .filter(hg -> player.getLevel() <= hg.maxLevel)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private static int getHourglassId(Hourglass hg) {
        return Rnd.get(hg.itemId);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("BuyHourglass")) {
            int val = Integer.parseInt(command.substring(13));
            Hourglass hg = getHourglass(player);
            int itemId = getHourglassId(hg);
            buyLimitedItem(player, "hourglass" + hg.minLevel + hg.maxLevel, itemId, val, false);
        } else if (command.startsWith("BuyVoice")) {
            buyLimitedItem(player, "nevitsVoice" + player.getAccountName(), 17094, 100000, true);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (val == 0) {
            Hourglass hg = getHourglass(player);
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile(getHtmlPath(getNpcId(), val, player));
            html.replace("%price%", hg.itemPrice);
            html.replace("%priceBreak%", Util.formatAdena(hg.itemPrice));
            html.replace("%minLvl%", hg.minLevel);
            html.replace("%maxLvl%", hg.maxLevel);
            player.sendPacket(html);
            return;
        }
        super.showChatWindow(player, val);
    }

    private void buyLimitedItem(Player player, String var, int itemId, int price, boolean isGlobalVar) {
        long _remaining_time;
        long _reuse_time = 20 * 60 * 60 * 1000;
        long _curr_time = System.currentTimeMillis();
        long _last_use_time = player.getVarLong(var);
        if (isGlobalVar) {
            Map<Integer, String> chars = player.getAccountChars();
            if (chars != null) {
                long use_time = 0;
                for (int objId : chars.keySet()) {
                    String val = Player.getVarFromPlayer(objId, var);
                    if (val != null)
                        if (Long.parseLong(val) > use_time)
                            use_time = Long.parseLong(val);
                }
                if (use_time > 0)
                    _last_use_time = use_time;
            }
        }

        if (player.isVarSet(var))
            _remaining_time = _curr_time - _last_use_time;
        else
            _remaining_time = _reuse_time;

        if (_remaining_time >= _reuse_time) {
            if (player.reduceAdena(price, true, "PriestOfBlessingInstance")) {
                addItem(player, itemId, 1);
                player.setVar(var, _curr_time);
            } else
                player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addItemName(57).addNumber(price));
        } else {
            int hours = (int) (_reuse_time - _remaining_time) / 3600000;
            int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
            if (hours > 0)
                player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
            else if (minutes > 0)
                player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
            else if (player.reduceAdena(price, true, "PriestOfBlessingInstance")) {
                addItem(player, itemId, 1);
                player.setVar(var, _curr_time);
            } else
                player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addItemName(57).addNumber(price));
        }
    }

    private static class Hourglass {
        final int minLevel;
        final int maxLevel;
        final int itemPrice;
        final List<Integer> itemId;

        Hourglass(int min, int max, int price, List<Integer> id) {
            minLevel = min;
            maxLevel = max;
            itemPrice = price;
            itemId = id;
        }
    }
}