package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.ChatType;

import java.util.List;


/**
 * This class handles following admin commands: - announce text = announces text
 * to all players - list_announcements = show menu - reload_announcements =
 * reloads announcements from txt file - announce_announcements = announce all
 * stored announcements to all players - add_announcement text = adds text to
 * startup announcements - del_announcement id = deletes announcement with
 * respective id
 */
public final class AdminAnnouncements implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanAnnounce)
            return false;

        switch (command) {
            case admin_list_announcements:
                listAnnouncements(activeChar);
                break;
            case admin_announce_menu:
                if ((fullString.length() > 20) && (fullString.length() <= 3020)) {
                    Announcements.INSTANCE.announceToAll(fullString.substring(20));
                }
                listAnnouncements(activeChar);
                break;
            case admin_announce_announcements:
                GameObjectsStorage.getAllPlayers().forEach(Announcements.INSTANCE::showAnnouncements);
                listAnnouncements(activeChar);
                break;
            case admin_add_announcement:
                if (wordList.length < 3)
                    return false;
                try {
                    int time = Integer.parseInt(wordList[1]);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < wordList.length; i++)
                        builder.append(" ").append(wordList[i]);

                    Announcements.INSTANCE.addAnnouncement(time, builder.toString(), true);
                    listAnnouncements(activeChar);
                } catch (Exception e) {
                }
                break;
            case admin_del_announcement:
                if (wordList.length != 2)
                    return false;
                int val = Integer.parseInt(wordList[1]);
                Announcements.INSTANCE.delAnnouncement(val);
                listAnnouncements(activeChar);
                break;
            case admin_announce:
                Announcements.INSTANCE.announceToAll(fullString.substring(15));
                break;
            case admin_a:
                String name = activeChar.getName();
                Announcements.INSTANCE.announceToAll(fullString.substring(8) + " (" + name + ")");
                break;
            case admin_crit_announce:
            case admin_c:
                if (wordList.length < 2)
                    return false;
                Announcements.INSTANCE.announceToAll(activeChar.getName() + ": " + fullString.replaceFirst("admin_crit_announce ", "").replaceFirst("admin_c ", ""), ChatType.CRITICAL_ANNOUNCE);
                break;
            case admin_cc:
                if (wordList.length < 2)
                    return false;
                Announcements.INSTANCE.announceToAll(fullString.replaceFirst("admin_cc ", ""), ChatType.COMMANDCHANNEL_ALL);
                break;
            case admin_toscreen:
            case admin_s:
                if (wordList.length < 2)
                    return false;
                String text = activeChar.getName() + ": " + fullString.replaceFirst("admin_toscreen ", "").replaceFirst("admin_s ", "");
                int time = 3000 + text.length() * 100; // 3 секунды + 100мс на символ
                ExShowScreenMessage sm = new ExShowScreenMessage(text, time);
                GameObjectsStorage.getAllPlayers().forEach(player -> player.sendPacket(sm));
                break;
            case admin_reload_announcements:
                Announcements.INSTANCE.loadAnnouncements();
                listAnnouncements(activeChar);
                activeChar.sendMessage("Announcements reloaded.");
                break;
        }

        return true;
    }

    private void listAnnouncements(Player activeChar) {
        List<Announcements.Announce> announcements = Announcements.INSTANCE.getAnnouncements();

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        StringBuilder replyMSG = new StringBuilder("<html noscrollbar><body>");
        replyMSG.append("<table cellpadding=0 cellspacing=0 width=292 height=356 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
        replyMSG.append("<tr>");
        replyMSG.append("<td align=center>");
        replyMSG.append("<br>");
        replyMSG.append("<table cellpadding=0 cellspacing=-5 width=260><tr>");
        replyMSG.append("<td><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("<td><button value=\"Events\" action=\"bypass -h admin_show_html events/events.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("<td><button value=\"Chars\" action=\"bypass -h admin_char_manage\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("<td><button value=\"Server\" action=\"bypass -h admin_server admserver.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("<td><button value=\"GM Shop\" action=\"bypass -h admin_gmshop\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br>");
        replyMSG.append("<table cellpadding=0 cellspacing=0 width=260><tr><td align=center><font color=LEVEL name=hs12>Announcement Menu</font></td></tr></table>");
        replyMSG.append("<br>");
        replyMSG.append("<multiedit var=\"new_announcement\" width=260 height=30><br>");
        replyMSG.append("<table cellpadding=0 cellspacing=0 width=260>");
        replyMSG.append("<tr><td align=center>Time(in seconds)</td>");
        replyMSG.append("<td><edit var=\"time\" type=\"number\" width=40 height=15></td></tr></table><br><br>");
        replyMSG.append("<table cellpadding=0 cellspacing=-5 width=260>");
        replyMSG.append("<tr>");
        replyMSG.append("<td><button value=\"Add\" action=\"bypass -h admin_add_announcement $time $new_announcement\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Announce\" action=\"bypass -h admin_announce_menu $new_announcement\" width=70 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Reload\" action=\"bypass -h admin_reload_announcements\" width=65 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Broadcast\" action=\"bypass -h admin_announce_announcements\" width=75 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br>");
        for (int i = 0; i < announcements.size(); i++) {
            Announcements.Announce announce = announcements.get(i);
            replyMSG.append("<table width=280><tr><td width=250>" + announce.getAnnounce() + "</td><td width=40>");
            replyMSG.append("<button value=\"Del\" action=\"bypass -h admin_del_announcement " + i + "\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
        }

        replyMSG.append("<br></td></tr></table></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_list_announcements,
        admin_announce_announcements,
        admin_add_announcement,
        admin_del_announcement,
        admin_announce,
        admin_a,
        admin_announce_menu,
        admin_crit_announce,
        admin_c,
        admin_cc,
        admin_toscreen,
        admin_s,
        admin_reload_announcements
    }
}