package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.item.ItemTemplate;

public final class ExpandInventory extends Functions {
    public void get() {
        if (player == null)
            return;

        if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED) {
            show("Service is disabled.", player);
            return;
        }

        if (player.getInventoryLimit() >= Config.SERVICES_EXPAND_INVENTORY_MAX) {
            player.sendMessage("Already max count.");
            return;
        }

        if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_INVENTORY_ITEM, Config.SERVICES_EXPAND_INVENTORY_PRICE, "ExpandInventory$get")) {
            player.setExpandInventory(player.getExpandInventory() + 1);
            player.setVar("ExpandInventory", player.getExpandInventory());
            player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
        } else if (Config.SERVICES_EXPAND_INVENTORY_ITEM == 57)
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
        else
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

        show();
    }

    private void show() {
        if (player == null)
            return;

        if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED) {
            show("Service is disabled.", player);
            return;
        }

        ItemTemplate item = ItemHolder.getTemplate(Config.SERVICES_EXPAND_INVENTORY_ITEM);

        String out = "";

        out += "<html><body>Extension equipment";
        out += "<br><br><table>";
        out += "<tr><td>Current size:</td><td>" + player.getInventoryLimit() + "</td></tr>";
        out += "<tr><td>Maximum size:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_MAX + "</td></tr>";
        out += "<tr><td>Cost slots:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_PRICE + " " + item.getName() + "</td></tr>";
        out += "</table><br><br>";
        out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.ExpandInventory:get\" value=\"Expand\">";
        out += "</body></html>";

        show(out, player);
    }
}