package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class BuyHero extends Functions {
    public void list() {
        Player player = getSelf();
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        String html = HtmCache.INSTANCE.getNotNull("scripts/services/BuyHero.htm", player);
        StringBuilder add = new StringBuilder();
        for (int i = 0; i < Config.SERVICES_HERO_SELL_DAY.size(); i++)
            add.append("<a action=\"bypass -h scripts_services.BuyHero:get ")
                    .append(i)
                    .append("\">")
                    .append("for ")
                    .append(Config.SERVICES_HERO_SELL_DAY.get(i))
                    .append(" days - ")
                    .append(Config.SERVICES_HERO_SELL_PRICE.get(i)).append(" ")
                    .append(ItemHolder.getTemplate(Config.SERVICES_HERO_SELL_ITEM.get(i)).getName()).append("</a><br>");
        html = html.replaceFirst("%toreplace%", add.toString());


        show(html, player);
    }

    public void get(String[] param) {
        Player player = getSelf();
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            show(HtmCache.INSTANCE.getNotNull("npcdefault.htm", player), player);
            return;
        }
        int i = toInt(param[0]);
        if ((Functions.getItemCount(player, Config.SERVICES_HERO_SELL_ITEM.get(i)) >= Config.SERVICES_HERO_SELL_PRICE.get(i))) {
            if (!player.isHero()) {
                player.setVar("HeroPeriod", (System.currentTimeMillis() + 60 * 1000 * 60 * 24 * Config.SERVICES_HERO_SELL_DAY.get(i)), -1);
                Functions.removeItem(player, Config.SERVICES_HERO_SELL_ITEM.get(i), Config.SERVICES_HERO_SELL_PRICE.get(i), "BuyHero$get");

                StatsSet hero = new StatsSet();
                hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
                hero.set(Olympiad.CHAR_ID, player.getObjectId());
                hero.set(Olympiad.CHAR_NAME, player.getName());
                hero.set(Hero.ACTIVE, 1);

                List<StatsSet> heroesToBe = new ArrayList<>();
                heroesToBe.add(hero);

                Hero.INSTANCE.computeNewHeroes(heroesToBe);
                player.setHero(true);
                Hero.addSkills(player);
                player.updatePledgeClass();
                if (player.isHero())
                    player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                player.broadcastUserInfo(true);
            }
        } else
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
    }
}