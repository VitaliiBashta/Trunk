package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class lvl extends Functions {
    public void list() {
        if (!Config.SERVICES_LVL_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        String html;

        html = HtmCache.INSTANCE.getNotNull("scripts/services/lvl.htm", player);
        String add = "";
        if (player.getLevel() < Config.SERVICES_LVL_UP_MAX)
            add += "<button value=\"Raise the occupation at1 (Price:" + Config.SERVICES_LVL_UP_PRICE + " " + ItemHolder.getTemplate(Config.SERVICES_LVL_UP_ITEM).getName() + ") \" action=\"bypass -h scripts_services.lvl:up" + "\" width=250 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">" + "</a><br>";
        if (player.getLevel() > Config.SERVICES_LVL_DOWN_MAX)
            add += "<button value=\"Lower occupation at the 1 (Price:" + Config.SERVICES_LVL_DOWN_PRICE + " " + ItemHolder.getTemplate(Config.SERVICES_LVL_DOWN_ITEM).getName() + ") \" action=\"bypass -h scripts_services.lvl:down" + "\" width=250 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">" + "</a><br>";
        html = html.replaceFirst("%toreplace%", add);

        show(html, player);
    }

    public void up() {
        if (!Config.SERVICES_LVL_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        int level = player.getLevel() + 1;
        if (player.haveItem(Config.SERVICES_LVL_UP_ITEM, Config.SERVICES_LVL_UP_PRICE)) {
            removeItem(player, Config.SERVICES_LVL_UP_ITEM, Config.SERVICES_LVL_UP_PRICE, "Level$up");
            setLevel(player, level);
        } else
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
    }

    public void down() {
        if (!Config.SERVICES_LVL_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        int level = player.getLevel() - 1;
        if (player.haveItem(Config.SERVICES_LVL_DOWN_ITEM, Config.SERVICES_LVL_DOWN_PRICE)) {
            removeItem(player, Config.SERVICES_LVL_DOWN_ITEM, Config.SERVICES_LVL_DOWN_PRICE, "Level$down");
            setLevel(player, level);
        } else
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
    }

    private void setLevel(Player player, int level) {
        long exp_add = Experience.LEVEL[level] - player.getExp();
        player.addExpAndSp(exp_add, 0);
    }
}