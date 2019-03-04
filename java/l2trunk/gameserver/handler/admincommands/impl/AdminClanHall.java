package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminClanHall implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        ClanHall clanhall = null;
        if (wordList.length > 1)
            clanhall = ResidenceHolder.getClanHall(toInt(wordList[1]));

        if (clanhall == null) {
            showClanHallSelectPage(activeChar);
            return true;
        }

        switch (comm) {
            case "admin_clanhall":
                showClanHallSelectPage(activeChar);
                break;
            case "admin_clanhallset":
                GameObject target = activeChar.getTarget();
                Player player = activeChar;
                if (target instanceof Player)
                    player = (Player) target;
                if (player.getClan() == null)
                    activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                else {
                    clanhall.changeOwner(player.getClan());
                }
                break;
            case "admin_clanhalldel":
                clanhall.changeOwner(null);
                break;
            case "admin_clanhallteleportself":
                Zone zone = clanhall.getZone();
                if (zone != null)
                    activeChar.teleToLocation(zone.getSpawn());
                break;
        }
        showClanHallPage(activeChar, clanhall);
        return true;
    }

    private void showClanHallSelectPage(Player activeChar) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String replyMSG = "<html><body>" + "<table width=268><tr>" +
                "<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td width=180><center><font color=\"LEVEL\">Clan Halls:</font></center></td>" +
                "<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "</tr></table><br>" +
                "<table width=268>" +
                "<tr><td width=130>ClanHall Name</td><td width=58>Town</td><td width=80>Owner</td></tr>" +
                "</table>" +
                "</body></html>";
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(adminReply);
    }

    private void showClanHallPage(Player activeChar, ClanHall clanhall) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>ClanHall Name</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_clanhall\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<center>");
        replyMSG.append("<br><br><br>ClanHall: ").append(clanhall.getName()).append("<br>");
        replyMSG.append("Location: &^").append(clanhall.getId()).append(";<br>");
        replyMSG.append("ClanHall Owner: ");
        Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.INSTANCE.getClan(clanhall.getOwnerId());
        if (owner == null)
            replyMSG.append("none");
        else
            replyMSG.append(owner.getName());

        replyMSG.append("<br><br><br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors ").append(clanhall.getId()).append("\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors ").append(clanhall.getId()).append("\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset ").append(clanhall.getId()).append("\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel ").append(clanhall.getId()).append("\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table><tr>");
        replyMSG.append("<td><button value=\"teleport getPlayer\" action=\"bypass -h admin_clanhallteleportself ").append(clanhall.getId()).append(" \" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("</center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_clanhall",
                "admin_clanhallset",
                "admin_clanhalldel",
                "admin_clanhallteleportself");
    }
}