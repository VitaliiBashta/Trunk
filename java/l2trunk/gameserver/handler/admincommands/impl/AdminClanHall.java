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

import static l2trunk.commons.lang.NumberUtils.toInt;

public class AdminClanHall implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        ClanHall clanhall = null;
        if (wordList.length > 1)
            clanhall = ResidenceHolder.getResidence(ClanHall.class, toInt(wordList[1]));

        if (clanhall == null) {
            showClanHallSelectPage(activeChar);
            return true;
        }

        switch (command) {
            case admin_clanhall:
                showClanHallSelectPage(activeChar);
                break;
            case admin_clanhallset:
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
            case admin_clanhalldel:
                clanhall.changeOwner(null);
                break;
            case admin_clanhallteleportself:
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

        StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=268><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center><font color=\"LEVEL\">Clan Halls:</font></center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table><br>");

        replyMSG.append("<table width=268>");
        replyMSG.append("<tr><td width=130>ClanHall Name</td><td width=58>Town</td><td width=80>Owner</td></tr>");

        // TODO: make sort by Location
		/*for(ClanHall clanhall : ClanHallManager.INSTANCE().getClanHalls().values())
			if (clanhall != null)
			{
				replyMSG.append("<tr><td>");
				replyMSG.append("<a action=\"bypass -h admin_clanhall " + clanhall.id() + "\">" + clanhall.name() + "</a>");
				replyMSG.append("</td><td>" + clanhall.getLocation() + "</td><td>");

				L2Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.INSTANCE().getClan(clanhall.getOwnerId());
				if (owner == null)
					replyMSG.append("none");
				else
					replyMSG.append(owner.name());

				replyMSG.append("</td></tr>");
			} */

        replyMSG.append("</table>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
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
        replyMSG.append("<br><br><br>ClanHall: " + clanhall.getName() + "<br>");
        replyMSG.append("Location: &^" + clanhall.getId() + ";<br>");
        replyMSG.append("ClanHall Owner: ");
        Clan owner = clanhall.getOwnerId() == 0 ? null : ClanTable.INSTANCE.getClan(clanhall.getOwnerId());
        if (owner == null)
            replyMSG.append("none");
        else
            replyMSG.append(owner.getName());

        replyMSG.append("<br><br><br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table>");
        replyMSG.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel " + clanhall.getId() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br>");
        replyMSG.append("<table><tr>");
        replyMSG.append("<td><button value=\"teleport player\" action=\"bypass -h admin_clanhallteleportself " + clanhall.getId() + " \" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("</center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_clanhall,
        admin_clanhallset,
        admin_clanhalldel,
        admin_clanhallteleportself
    }
}