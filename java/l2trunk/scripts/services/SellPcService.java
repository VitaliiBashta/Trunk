package l2trunk.scripts.services;


import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class SellPcService extends Functions implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(Player.class);

    public void dialog() {
        if (player == null)
            return;

        show("scripts/services/SellPcService.htm", player);
    }


    public void pay(String[] param) {
        if (player == null)
            return;

        int points = toInt(param[0]);    //поинты (очки)
        int itemId = toInt(param[1]);    //ид предмета, который взымается
        int itemCount = toInt(param[2]); //количество предмета, который взымается

        ItemTemplate item = ItemHolder.getTemplate(itemId); //id итема

        if (item == null)
            return;

        ItemInstance pay = player.inventory.getItemByItemId(item.itemId());
        if (pay != null && pay.getCount() >= itemCount) //кол-во денег
        {
            player.addPcBangPoints(points, false);
            player.getInventory().destroyItem(pay, itemCount, "SellPcService");
            player.sendMessage("You have purchased " + points + " PC-Points");
        } else
            player.sendMessage("You are not " + item.getName());
    }

    @Override
    public void onLoad() {
        _log.info("Loaded Service: SellPcService");
    }

}
