package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

/**
 * Admin comand handler for Manor System
 * This class handles following admin commands:
 * - manor_info = shows info about current manor state
 * - manor_approve = approves settings for the next manor period
 * - manor_setnext = changes manor settings to the next day's
 * - manor_reset castle = resets all manor data for specified castle (or all)
 * - manor_setmaintenance = sets manor system under maintenance mode
 * - manor_save = saves all manor data into database
 * - manor_disable = disables manor system
 */
public final class AdminManor implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu)
            return false;

        StringTokenizer st = new StringTokenizer(fullString);
        fullString = st.nextToken();

        if (fullString.equals("admin_manor"))
            showMainPage(activeChar);
        else if (fullString.equals("admin_manor_reset")) {
            int castleId = toInt(st.nextToken());

            if (castleId > 0) {
                Castle castle = ResidenceHolder.getResidence(Castle.class, castleId);
                castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
                castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
                castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
                castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
                castle.saveCropData();
                castle.saveSeedData();
                activeChar.sendMessage("Manor data for " + castle.getName() + " was nulled");
            } else {
                for (Castle castle : ResidenceHolder.getResidenceList(Castle.class)) {
                    castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
                    castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
                    castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
                    castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
                    castle.saveCropData();
                    castle.saveSeedData();
                }
                activeChar.sendMessage("Manor data was nulled");
            }
            showMainPage(activeChar);
        } else if (fullString.equals("admin_manor_save")) {
            CastleManorManager.INSTANCE.save();
            activeChar.sendMessage("Manor System: all data saved");
            showMainPage(activeChar);
        } else if (fullString.equals("admin_manor_disable")) {
            boolean mode = CastleManorManager.INSTANCE.isDisabled();
            CastleManorManager.INSTANCE.setDisabled(!mode);
            if (mode)
                activeChar.sendMessage("Manor System: enabled");
            else
                activeChar.sendMessage("Manor System: disabled");
            showMainPage(activeChar);
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void showMainPage(Player activeChar) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body>");

        replyMSG.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
        replyMSG.append("<table width=\"100%\">");
        replyMSG.append("<tr><td>Disabled: " + (CastleManorManager.INSTANCE.isDisabled() ? "yes" : "no") + "</td>");
        replyMSG.append("<td>Under Maintenance: " + (CastleManorManager.INSTANCE.isUnderMaintenance() ? "yes" : "no") + "</td></tr>");
        replyMSG.append("<tr><td>Approved: " + (ServerVariables.getBool("ManorApproved") ? "yes" : "no") + "</td></tr>");
        replyMSG.append("</table>");

        replyMSG.append("<center><table>");
        replyMSG.append("<tr><td><button value=\"" + (CastleManorManager.INSTANCE.isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Reset\" action=\"bypass -h admin_manor_reset\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table></center>");

        replyMSG.append("<br><center>Castle Information:<table width=\"100%\">");
        replyMSG.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");

        ResidenceHolder.getResidenceList(Castle.class).forEach(c ->
                replyMSG.append("<tr><td>")
                        .append(c.getName())
                        .append("</td>")
                        .append("<td>")
                        .append(c.getManorCost(CastleManorManager.PERIOD_CURRENT))
                        .append("a</td><td>")
                        .append(c.getManorCost(CastleManorManager.PERIOD_NEXT))
                        .append("a</td></tr>"));

        replyMSG.append("</table><br></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private enum Commands {
        admin_manor,
        admin_manor_reset,
        admin_manor_save,
        admin_manor_disable
    }
}