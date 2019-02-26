package l2trunk.scripts.services;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ExchangeItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.exchange.Change;
import l2trunk.gameserver.model.exchange.Variant;
import l2trunk.gameserver.model.items.ItemAttributes;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Exchanger extends Functions {
    public void change_page(String[] arg) {
        if (player == null)
            return;

        if (!Config.SERVICES_EXCHANGE_EQUIP) {
            show("Service is disabled.", player);
            return;
        }

        boolean isUpgrade = arg[0].equalsIgnoreCase("1");

        removeVars(true);
        cleanAtt(-1);
        NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/Exchanger/page.htm");
        String template = HtmCache.INSTANCE.getNotNull("scripts/services/Exchanger/template.htm", player);
        String block = "";
        String list = "";
        List<Change> _list = new ArrayList<>();
        //TODO it dosent change the armor throw ERROR Script class services not found!
        for (ItemInstance item : player.getInventory().getPaperdollItems()) {
            if (item != null) {
                Change change = ExchangeItemHolder.getChanges(item.getItemId(), isUpgrade);
                if (change != null) {
                    _list.add(change);
                }
            }
        }

        // If the list is empty then we just send a html with the message
        if (_list.isEmpty()) {
            NpcHtmlMessage html2 = new NpcHtmlMessage(5);
            html2.setHtml("<html><title>" + (isUpgrade ? "Upgrade Items" : "Exchange Items") + "</title><body><center><br><br><font name=hs12>Wear the item to see it on the list.</font></center></body></html>");
            player.sendPacket(html2);
            return;
        }

        int perpage = 6;
        int page = (arg.length > 1) && (Util.isNumber(arg[1])) ? toInt(arg[1]) : 1;
        int counter = 0;
        for (int i = (page - 1) * perpage; i < _list.size(); i++) {
            Change pack = _list.get(i);
            block = template;
            block = block.replace("{bypass}", "bypass -h scripts_services.Exchanger:change_list " + pack.getId() + " " + (isUpgrade ? 1 : 0));  //ERROR Script class services not found!
            block = block.replace("{name}", pack.getName());
            block = block.replace("{icon}", pack.getIcon());
            block = block.replace("{cost}", new CustomMessage("scripts.services.cost").addString(Util.formatPay(player, pack.getCostCount(), pack.getCostId())).toString());
            list = list + block;

            counter++;
            if (counter >= 6) {
                break;
            }
        }
        double count = Math.ceil(_list.size() / 6.0D);

        int inline = 1;
        String navigation = "";
        for (int i = 1; i <= count; i++) {
            if (i == page) {
                navigation = navigation + "<td width=25 align=center valign=top><button value=\"[" + i + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
            } else {
                navigation = navigation + "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.Exchanger:change_page " + (isUpgrade ? 1 : 0) + " " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
            }
            if (inline % 7 == 0) {
                navigation = navigation + "</tr><tr>";
            }
            inline++;
        }
        if (inline == 2) {
            navigation = "<td width=30 align=center valign=top>...</td>";
        }
        html.replace("%list%", list);
        html.replace("%navigation%", navigation);

        player.sendPacket(html);
    }

    public void change_list() {

    }

    public void change_list(String[] arg) {
        if (player == null)
            return;

        cleanAtt(-1);
        removeVars(true);
        if ((arg[0].isEmpty()) || (!Util.isNumber(arg[0]))) {
            return;
        }
        int id = toInt(arg[0]);
        boolean isUpgrade = arg[1].equalsIgnoreCase("1");

        NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/Exchanger/list.htm");
        String template = HtmCache.INSTANCE.getNotNull("scripts/services/Exchanger/template.htm", player);
        String block;
        String list = "";

        Change change = ExchangeItemHolder.getChanges(id, isUpgrade);
        if (change == null) {
            return;
        }
        player.addQuickVar("exchange", id);

        List<Variant> _list = change.getList();

        int perpage = 6;
        int page = (arg.length > 2) && (!arg[2].isEmpty()) && (Util.isNumber(arg[2])) ? toInt(arg[2]) : 1;
        int counter = 0;
        for (int i = (page - 1) * perpage; i < _list.size(); i++) {
            Variant pack = _list.get(i);
            block = template;
            block = block.replace("{bypass}", "bypass -h scripts_services.Exchanger:change_open " + pack.number + " " + (isUpgrade ? 1 : 0));
            block = block.replace("{name}", pack.name);
            block = block.replace("{icon}", pack.icon);
            block = block.replace("{cost}", new CustomMessage("scripts.services.cost").addString(Util.formatPay(player, change.getCostCount(), change.getCostId())).toString());
            list = list + block;

            counter++;
            if (counter >= 6) {
                break;
            }
        }
        double count = Math.ceil(_list.size() / perpage);

        int inline = 1;
        String navigation = "";
        for (int i = 1; i <= count; i++) {
            if (i == page) {
                navigation = navigation + "<td width=25 align=center valign=top><button value=\"[" + i + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
            } else {
                navigation = navigation + "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.Exchanger:change_list " + id + " " + (isUpgrade ? 1 : 0) + " " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
            }
            if (inline % 7 == 0) {
                navigation = navigation + "</tr><tr>";
            }
            inline++;
        }
        if (inline == 2) {
            navigation = "<td width=30 align=center valign=top>...</td>";
        }
        html.replace("%list%", list);
        html.replace("%navigation%", navigation);
        html.replace("%choice%", change.getName());

        player.sendPacket(html);
    }

    public void change_att(String[] arg) {
        if (player == null)
            return;

        if ((arg[0].isEmpty()) || (!Util.isNumber(arg[0]))) {
            return;
        }
        int obj_my = player.getQuickVarI("exchange_obj");
        if (obj_my == -1) {
            return;
        }
        ItemInstance item = player.getInventory().getItemByObjectId(obj_my);
        if (item == null) {
            return;
        }
        int id_new = player.getQuickVarI("exchange_number");
        if (id_new == -1) {
            return;
        }
        int att_id = toInt(arg[0]);
        boolean isUpgrade = "1".equalsIgnoreCase(arg[1]);
        Element att = Element.getElementById(att_id);
        if (att != Element.NONE) {
            player.addQuickVar("ex_att_" + att_id, item.getAttributeElementValue());
            player.addQuickVar("ex_att", att_id);
            cleanAtt(att_id);
        }
        change_open(new String[]{String.valueOf(id_new), (isUpgrade ? "1" : "0")});
    }

    private void cleanAtt(int exclude) {
        if (player == null)
            return;

        for (Element att : Element.VALUES) {
            if (att.getId() != exclude) {
                player.deleteQuickVar("ex_att_" + att.getId());
            }
        }
        if (exclude == -1) {
            player.deleteQuickVar("ex_att");
        }
    }

    private void change_open(String[] arg) {
        if (player == null)
            return;

        int id = player.getQuickVarI("exchange");
        if ((id == -1) || (arg[0].isEmpty()) || (!Util.isNumber(arg[0]))) {
            return;
        }
        int new_id = toInt(arg[0]);
        boolean isUpgrade = "1".equals(arg[1]);
        ItemInstance item = null;
        Change change = null;
        for (ItemInstance inv : player.getInventory().getPaperdollItems()) {
            if (inv != null) {
                change = ExchangeItemHolder.getChanges(inv.getItemId(), isUpgrade);
                if ((change != null) && (change.getId() == id)) {
                    item = inv;
                    break;
                }
            }
        }
        if (item == null) {
            return;
        }
        Variant variant = change.getVariant(new_id);
        if (variant == null) {
            return;
        }
        removeVars(false);
        player.addQuickVar("exchange_obj", item.objectId());
        player.addQuickVar("exchange_new", variant.id);
        player.addQuickVar("exchange_attribute", change.attChange());
        if (change.attChange()) {
            player.addQuickVar("exchange_number", variant.number);
        }
        NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/Exchanger/general.htm");
        html.replace("%my_name%", item.getName());
        html.replace("%my_ench%", "+" + item.getEnchantLevel());
        html.replace("%my_icon%", item.getTemplate().getIcon());
        ItemAttributes att = item.getAttributes();
        if ((!change.attChange()) || (item.getAttributeElementValue() == 0)) {
            String att_info = HtmCache.INSTANCE.getNotNull("scripts/services/Exchanger/att_info.htm", player);
            att_info = att_info.replace("%Earth%", String.valueOf(att.getEarth()));
            att_info = att_info.replace("%Fire%", String.valueOf(att.getFire()));
            att_info = att_info.replace("%Holy%", String.valueOf(att.getHoly()));
            att_info = att_info.replace("%Unholy%", String.valueOf(att.getUnholy()));
            att_info = att_info.replace("%Water%", String.valueOf(att.getWater()));
            att_info = att_info.replace("%Wind%", String.valueOf(att.getWind()));
            html.replace("%att_info%", att_info);
        } else {
            String att_info = HtmCache.INSTANCE.getNotNull("scripts/services/Exchanger/att_change.htm", player);
            if (player.getQuickVarI("ex_att") == -1) {
                att_info = att_info.replace("%Earth%", String.valueOf(att.getEarth()));
                att_info = att_info.replace("%Fire%", String.valueOf(att.getFire()));
                att_info = att_info.replace("%Holy%", String.valueOf(att.getHoly()));
                att_info = att_info.replace("%Unholy%", String.valueOf(att.getUnholy()));
                att_info = att_info.replace("%Water%", String.valueOf(att.getWater()));
                att_info = att_info.replace("%Wind%", String.valueOf(att.getWind()));
            } else {
                att_info = att_info.replace("%Fire%", String.valueOf(player.getQuickVarI("ex_att_0", 0)));
                att_info = att_info.replace("%Water%", String.valueOf(player.getQuickVarI("ex_att_1", 0)));
                att_info = att_info.replace("%Wind%", String.valueOf(player.getQuickVarI("ex_att_2", 0)));
                att_info = att_info.replace("%Earth%", String.valueOf(player.getQuickVarI("ex_att_3", 0)));
                att_info = att_info.replace("%Holy%", String.valueOf(player.getQuickVarI("ex_att_4", 0)));
                att_info = att_info.replace("%Unholy%", String.valueOf(player.getQuickVarI("ex_att_5", 0)));
            }
            html.replace("%att_info%", att_info);
        }
        html.replace("%cost%", Util.formatPay(player, change.getCostCount(), change.getCostId()));
        html.replace("%new_name%", variant.name);
        html.replace("%new_icon%", variant.icon);
        html.replace("%new_id%", id);
        html.replace("%is_upgrade%", (isUpgrade ? 1 : 0));

        player.sendPacket(html);
    }

    public void exchange(String[] arg) {
        if (player == null)
            return;

        int exchangeId = player.getQuickVarI("exchange");
        if (exchangeId == -1) {
            return;
        }
        int obj_my = player.getQuickVarI("exchange_obj");
        if (obj_my == -1) {
            return;
        }
        int id_new = player.getQuickVarI("exchange_new");
        if (id_new == -1) {
            return;
        }

        boolean isUpgrade = "1".equalsIgnoreCase(arg[0]);
        boolean att_change = player.getQuickVarB("exchange_attribute", false);

        Change change = ExchangeItemHolder.getChanges(exchangeId, isUpgrade);
        if (change == null)
            return;

        PcInventory inv = player.getInventory();
        ItemInstance item_my = inv.getItemByObjectId(obj_my);
        if (item_my == null) {
            return;
        }

        if (!player.haveItem(change.getCostId(), change.getCostCount())) {
            if (change.getCostId() == 57)
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            else
                player.sendMessage("You don't have " + change.getCostCount() + " Donator Coins!");
            return;
        }
        removeItem(player, change.getCostId(), change.getCostCount(), "Exchangers$exchange");

        ItemInstance item = ItemFunctions.createItem(id_new);
        item.setEnchantLevel(item_my.getEnchantLevel());
        item.setAugmentation(item_my.getAugmentationMineralId(), item_my.getAugmentations());

        int new_att = player.getQuickVarI("ex_att");
        if ((att_change) && (new_att != -1)) {
            Element element = Element.getElementById(new_att);
            int val = item_my.getAttributeElementValue();
            if (val > 0) {
                item.setAttributeElement(element, val);
            }
        } else {
            for (Element element : Element.VALUES) {
                int val = item_my.getAttributes().getValue(element);
                if (val > 0) {
                    item.setAttributeElement(element, val);
                }
            }
        }
        String msg = "You exchange item " + item_my.getName() + " to " + item.getName();
        if (inv.destroyItemByObjectId(item_my.objectId(), item_my.getCount(), "destroied")) {
            inv.addItem(item, "added the item");
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();
            if (ItemFunctions.checkIfCanEquip(player, item) == null) {
                inv.equipItem(item);
            }
            player.sendMessage(msg);
        } else {
            item.deleteMe();
        }
        removeVars(true);
        cleanAtt(-1);
    }

    private void removeVars(boolean exchange) {
        if (player == null)
            return;

        if (exchange) {
            player.deleteQuickVar("exchange");
        }
        player.deleteQuickVar("exchange_obj");
        player.deleteQuickVar("exchange_new");
        player.deleteQuickVar("exchange_attribute");
    }
}
