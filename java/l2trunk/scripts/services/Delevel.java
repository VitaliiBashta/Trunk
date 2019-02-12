package l2trunk.scripts.services;


import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Util;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Delevel extends Functions {
    public void delevel_page() {
        if (player == null) {
            return;
        }
        String append = "Delevel Service";
        append += "<br>";
        append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Delevel.DelevelFor", player).addString(Util.formatAdena(Config.SERVICES_DELEVEL_COUNT)).addItemName(Config.SERVICES_DELEVEL_ITEM) + "</font>";
        append += "<table>";
        append += "<tr><td></td></tr>";
        append += "<tr><td><button value=\"Delevel\" action=\"bypass -h scripts_services.Delevel:delevel\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
        append += "</table>";
        show(append, player);
    }

    public void delevel() {
        if (!Config.SERVICES_DELEVEL_ENABLED) {
            player.sendMessage("This service is not available.");
            return;
        }
        if (player.getLevel() <= Config.SERVICES_DELEVEL_MIN_LEVEL) {
            player.sendMessage("This service is available to characters with " + Config.SERVICES_DELEVEL_MIN_LEVEL + " occupation.");
        } else if (!player.haveItem(Config.SERVICES_DELEVEL_ITEM, Config.SERVICES_DELEVEL_COUNT)) {
            player.sendMessage("You don't have enought items.");
        } else {
            long pXp = player.getExp();
            long tXp = Experience.LEVEL[(player.getLevel() - 1)];
            if (pXp <= tXp) {
                return;
            }
            removeItem(player, Config.SERVICES_DELEVEL_ITEM, Config.SERVICES_DELEVEL_COUNT, "delevel");
            player.addExpAndSp(-(pXp - tXp), 0);
        }
    }

}