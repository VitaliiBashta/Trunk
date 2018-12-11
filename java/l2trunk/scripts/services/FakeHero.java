package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.DeclensionKey;
import l2trunk.gameserver.utils.Util;

import java.util.List;

public final class FakeHero extends Functions {
    private final List<Integer> ITEM = Config.SERVICES_HERO_SELL_ITEM;
    private final List<Integer> PRICE = Config.SERVICES_HERO_SELL_PRICE;
    private final List<Integer> DAY = Config.SERVICES_HERO_SELL_DAY;

    public void list(String[] arg) {
        final Player player = getSelf();
        if (player == null)
            return;

        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            player.sendMessage("This service is turned off.");
            return;
        }
        NpcHtmlMessage html;
        if ((!player.isHero()) && (!player.isFakeHero())) {
            html = new NpcHtmlMessage(5).setFile("scripts/services/FakeHero/index.htm");
            String template = HtmCache.INSTANCE.getNotNull("scripts/services/FakeHero/template.htm", player);
            String block = "";
            String list = "";

            int page = arg[0].length() > 0 ? Integer.parseInt(arg[0]) : 1;
            int counter = 0;
            for (int i = (page - 1) * 6; i < DAY.size(); i++) {
                block = template;
                block = block.replace("{bypass}", "bypass -h scripts_services.FakeHero:buy " + i);
                block = block.replace("{info}", DAY.get(i) + " " + Util.declension(DAY.get(i), DeclensionKey.DAYS));
                block = block.replace("{cost}", new CustomMessage("<font color=00ff00>Cost</font>: {0}")
                        .addString(Util.formatPay(player, PRICE.get(i), ITEM.get(i))).toString());
                list = list + block;

                counter++;
                if (counter >= 6) {
                    break;
                }
            }
            double count = Math.ceil(DAY.size() / 6.0D);
            int inline = 1;
            String navigation = "";
            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation = navigation + "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h scripts_services.FakeHero:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation = navigation + "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.FakeHero:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
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
        } else {
            html = new NpcHtmlMessage(5).setFile("scripts/services/FakeHero/already.htm");
            player.sendMessage(new CustomMessage("You are already a hero."));
        }
        player.sendPacket(html);
    }

    public void buy(String[] arg) {
        final Player player = getSelf();
        if (player == null)
            return;

        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            player.sendMessage("This service is turned off.");
            return;
        }
        if (arg[0].isEmpty()) {
            return;
        }
        int i = Integer.parseInt(arg[0]);
        if (i > Config.SERVICES_HERO_SELL_DAY.size()) {
            player.sendMessage("Error.");
            return;
        }
        if ((player.isHero()) || (player.isFakeHero())) {
            player.sendMessage("You are already a hero.");
            return;
        }
        if (Util.getPay(player, ITEM.get(i), PRICE.get(i), true)) {
            long day = Util.addDay(DAY.get(i));
            long time = System.currentTimeMillis() + day;
            try {
                player.setVar("hasFakeHero", 1, time);
                player.sendChanges();
                player.broadcastCharInfo();
                if (Config.SERVICES_HERO_SELL_SKILL) {
                    Hero.addSkills(player);
                }
                player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.GIVE_HERO));
                player.sendMessage(new CustomMessage("Congratulations!").addNumber(DAY.get(i)).addString(Util.declension(DAY.get(i), DeclensionKey.DAYS)));
            } catch (Exception e) {
                player.sendMessage(new CustomMessage("Error."));
            }
        }
    }
}
