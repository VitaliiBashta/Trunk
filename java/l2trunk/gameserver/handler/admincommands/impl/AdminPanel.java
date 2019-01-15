package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.clientpackets.Say2C;
import l2trunk.gameserver.network.serverpackets.CreatureSay;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.GmListTable;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminPanel implements IAdminCommandHandler {
    private static void CppanelMainPage(Player activeChar) {
        int count = ServerVariables.getInt("fake_players");

        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>CPPanel</title></head><body><center><br>");
        html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        html.append("<br>");
        html.append("Current FakePlayers: <font color=LEVEL>" + count + "</font><br>");
        html.append("<br><br>");
        html.append("Change FakePlayers Num: <edit var=\"value\" width=100 height=15><br>");
        html.append("<button value=\"Do it!\" action=\"bypass -h admin_changevaluescppanel $value\" width=95 height=21 back=\"L2UI_ch3.bigbutton_down\" fore=\"L2UI_ch3.bigbutton\">");
        html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center></body></html>");
        nhm.setHtml(html.toString());
        activeChar.sendPacket(nhm);
    }

    private static void clanSkillList(Player player) {
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder html = new StringBuilder("");
        html.append("<html><head><title>Clan Skill Panel</title></head><body>");
        html.append("<center><table width=280 bgcolor=222120><tr><td><center>");
        html.append("<font name=\"hs12\" color=\"FF0032\">L2Mythras</font></center></td></tr></table>");
        html.append("<br><br><img src=\"L2UI.squaregray\" width=\"280\" height=\"2\">");
        html.append("<table width=300 bgcolor=1F1818><tr><td width=220><center><font color=c1b33a>Option</font></center></td>");
        html.append("<td width=140><center><font color=c1b33a>Status</font></center></td></tr></table>");

        html.append("<img src=\"L2UI.squaregray\" width=\"280\" height=\"2\"><table width=280 bgcolor=222120>");

        int lvl = 3;
        for (int i = 370; i <= 391; i++) {
            if (i == 391)
                lvl = 1;

            Skill skill = SkillTable.INSTANCE.getInfo(i, lvl);

            html.append("<tr>");
            html.append("<td width=230 align=left>");
            html.append("<center><font color=878080>" + skill.getName() + "</font></center>");
            html.append("</td>");
            html.append("<td width=70>");
            html.append("<center>");
            html.append("<font color=c1b33a><a action=\"bypass -h admin_clanskills " + i + " " + lvl + "\">Add " + lvl + " Level</a></font>");
            html.append("</center>");
            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");
        nhm.setHtml(html.toString());
        player.sendPacket(nhm);
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        StringTokenizer st = new StringTokenizer(fullString);

        Commands command = Commands.valueOf(st.nextToken());

        if (comm == null)
            return false;

        NpcHtmlMessage html;
        String text = null;
        GameObject target;
        Player caster = null;
        Party p;
        final List<Player> world = GameObjectsStorage.getAllPlayers();

        switch (command) {
            case admin_panel:
                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/panel.htm");
                activeChar.sendPacket(html);
                break;
            case admin_controlpanelchar:
                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;

            case admin_effects:
                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/effects.htm");
                activeChar.sendPacket(html);
                break;

            case admin_imitate:
                target = activeChar.getTarget();

                if (target == null) {
                    activeChar.sendMessage("Target incorrect");
                    return false;
                }

                if (st.hasMoreTokens())
                    text = fullString.substring(14);
                String t = text;
                World.getAroundPlayers(activeChar)
                        .forEach(pl -> pl.sendPacket(new CreatureSay(target.getObjectId(), 0, target.getName(), t)));

                activeChar.sendPacket(new CreatureSay(target.getObjectId(), 0, target.getName(), text));
                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;

            case admin_sendexmsg:

                String text2 = fullString.substring(15);
                if (!text2.isEmpty()) {
                    world.forEach(pl ->
                            pl.sendPacket(new ExShowScreenMessage(text2)));
                }

                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);

                break;
            case admin_sendcsmsg:

                text = fullString.substring(15);

                if (!"".equals(text)) {
                    String text3 = text.substring(1);
                    world.forEach(player -> player.sendPacket(new CreatureSay(0, 15, activeChar.getName(), text3)));
                }


                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);

                break;

            case admin_sit_down:

                activeChar.getTarget().getPlayer().sitDown(null);

                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;

            case admin_sit_down_party:
                target = activeChar.getTarget().getPlayer();

                p = target.getPlayer().getParty();

                if (p == null) {
                    activeChar.sendMessage("This char has not party.");
                    return false;
                }

                if (!p.isLeader(caster)) {
                    activeChar.sendMessage("You must use it on leader only. Leader of this party is " + p.getLeader().getName());
                    return false;
                }

                p.getMembers().forEach(ppl -> ppl.sitDown(null));

                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;

            case admin_stand_up:
                activeChar.getTarget().getPlayer().standUp();
                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;

            case admin_stand_up_party:
                p = activeChar.getTarget().getPlayer().getParty();

                if (p == null) {
                    activeChar.sendMessage("This char has not party.");
                    return false;
                }

                if (!p.isLeader(caster)) {
                    activeChar.sendMessage("You must use it on leader only. Leader of this party is " + p.getLeader().getName());
                    return false;
                }

                p.getMembers().forEach(Player::standUp);

                html = new NpcHtmlMessage(5);
                html.setFile("admin/panel/controlpanel.htm");
                activeChar.sendPacket(html);
                break;
            case admin_smallfirework:
                world.forEach(pl -> pl.broadcastPacket(new MagicSkillUse(pl, 2023)));

                break;
            case admin_mediumfirework:
                world.forEach(pl -> pl.broadcastPacket(new MagicSkillUse(pl, 2024)));
                break;
            case admin_bigfirework:
                world.forEach(pl -> pl.broadcastPacket(new MagicSkillUse(pl, 2025)));
                break;
            case admin_cppanel:
                CppanelMainPage(activeChar);
                break;
            case admin_changevaluescppanel:
                if (st.hasMoreTokens()) {
                    int value = toInt(st.nextToken(), 0);
                    ServerVariables.set("fake_players", value);

                    activeChar.sendMessage("You put " + value + " fakeplayers on database.");
                    CppanelMainPage(activeChar);
                }
                break;
            case admin_clanskills:

                if (!st.hasMoreTokens()) {
                    clanSkillList(activeChar);
                    return false;
                }

                if (activeChar.getTarget() == null) {
                    clanSkillList(activeChar);
                    activeChar.sendMessage("Invalid Target.");
                    return false;
                }

                target = activeChar.getTarget().getPlayer();

                int skillId = toInt(st.nextToken());
                int skillLevel = toInt(st.nextToken());
                Skill skill = SkillTable.INSTANCE.getInfo(skillId, skillLevel);
                if (skill != null && target.getPlayer().getClan() != null) {
                    Clan clan = target.getPlayer().getClan();
                    clan.addSkill(skill, true);
                    target.getPlayer().sendMessage("Admin add to your clan " + skill.getName() + " skill.");
                    activeChar.sendMessage("You add " + skill.getName() + " skill to the clan " + clan.getName());
                }
                clanSkillList(activeChar);
                break;
            case admin_lockshout:
                Say2C.LOCK_SHOUT_VOICE = !Say2C.LOCK_SHOUT_VOICE;
                activeChar.sendMessage("Bool of Shout reversed to " + Say2C.LOCK_SHOUT_VOICE);
                break;
            case admin_lockhero:
                Say2C.LOCK_HERO_VOICE = !Say2C.LOCK_HERO_VOICE;
                activeChar.sendMessage("Bool of Hero reversed to " + Say2C.LOCK_HERO_VOICE);
                break;
            case admin_cleanup:
                System.runFinalization();
                System.gc();
                System.out.println("Java Memory Cleanup.");
                break;
        }
        return true;
    }

    public String showItems(Playable target) {
        String items = "";

        String lHand = "---";
        if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) != null)
            lHand = target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).getName() + " <button value=\"Augment it\" action=\"bypass -h admin_setaugmentonweapon 1 $id $lvl\" width=180 height=32 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">";

        String rHand = "---";
        if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null)
            rHand = target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getName() + " <button value=\"Augment it\" action=\"bypass -h admin_setaugmentonweapon 2 $id $lvl\" width=180 height=32 back=\"L2UI_CH3.refinegrade3_21\" fore=\"L2UI_CH3.refinegrade3_21\">";

        items += "</font><br><font color=\"FF00FF\"> LHand: </font><font color=\"LEVEL\">" + lHand +
                "</font><br><font color=\"FF00FF\"> RHand: </font><font color=\"LEVEL\">" + rHand +

                "</font><br>";
        return items;

    }

    public void doHero(Player activeChar, Player _player, String _playername, String _time) {
        int days = Integer.parseInt(_time);

        if (_player == null) {
            activeChar.sendMessage("not found char" + _playername);
            return;
        }

        if (days > 0) {
            long expire = ((long) 60 * 1000 * 60 * 24 * days);
            _player.setHero(true);
            Hero.addSkills(_player);
            _player.setVar("DonateHero", "true", System.currentTimeMillis() + expire);
            GmListTable.broadcastMessageToGMs("GM " + activeChar.getName() + " set hero stat for player " + _playername + " for " + _time + " day(s)");
            _player.sendMessage(activeChar.getName() + ", added you hero for " + days + " days.");
            activeChar.sendMessage("The hero status added to " + _player.getName() + " for " + days + " days.");

            _player.broadcastCharInfo();
        } else {
            activeChar.sendMessage("You must put up to 1 day for hero.");
        }
    }

    public void removeHero(Player activeChar, Player _player, String _playername) {
        _player.setHero(false);
        Hero.removeSkills(_player);
        _player.unsetVar("DonateHero");

        GmListTable.broadcastMessageToGMs("GM " + activeChar.getName() + " remove hero stat of player " + _playername);
        _player.sendMessage("Your hero status removed by admin.");
        activeChar.sendMessage("The hero status removed from: " + _player.getName());

        _player.broadcastCharInfo();
    }


    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_panel,
        admin_controlpanelchar,
        admin_imitate,
        admin_sendexmsg,
        admin_sendcsmsg,
        admin_sit_down,
        admin_sit_down_party,
        admin_stand_up,
        admin_stand_up_party,

        admin_effects,
        admin_smallfirework,
        admin_mediumfirework,
        admin_bigfirework,
        admin_cppanel,
        admin_changevaluescppanel,
        admin_lockshout,
        admin_lockhero,
        admin_cleanup,
        admin_clanskills,
    }
}
