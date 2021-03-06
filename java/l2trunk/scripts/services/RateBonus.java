package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class RateBonus extends Functions {
    private static final List<Integer> PKS = List.of(1, 2, 5, 10, 25, 50, 100, 250, 500, 1000);

    public void list() {
        if (!Config.SERVICES_WASH_PK_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        String html;

        html = HtmCache.INSTANCE.getNotNull("scripts/services/Eleanor.htm", player);
        StringBuilder add = new StringBuilder();

        for (int pk : PKS) {
            if (player.getPkKills() <= pk)
                break;
            add.append(getPkButton(pk));
        }

        add.append(getPkButton(player.getPkKills()));

        html = html.replaceFirst("%toreplace%", add.toString());


        show(html, player);
    }

    private static String getPkButton(int i) {
        return "<a action=\"bypass -h scripts_services.RateBonus:get " + i + "\"> for " + i + " PK - "
                + Config.SERVICES_WASH_PK_PRICE * i
                + " " + ItemHolder.getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "</a><br>";
    }

    public void get(String[] param) {
        if (!Config.SERVICES_WASH_PK_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        int i = Integer.parseInt(param[0]);
        if (!player.getInventory().destroyItemByItemId(Config.SERVICES_WASH_PK_ITEM, Config.SERVICES_WASH_PK_PRICE * i, "BuyWashPk$get")) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        } else {
            int kills = player.getPkKills();
            player.setPkKills(kills - i);
            player.broadcastCharInfo();
        }
    }
}