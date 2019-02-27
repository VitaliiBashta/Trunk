package l2trunk.gameserver.handler.admincommands.impl;


import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdminMail implements IAdminCommandHandler {
    public static final String MAIL_ALL_TEXT = "MAIL_ALL";
    public static final String MAIL_LIST = "MAIL_LIST";
    private static final Map<Integer, List<String>> mailNicks = new HashMap<>();

    private static void showList(Player activeChar) {
        NpcHtmlMessage msg = new NpcHtmlMessage(0);

        StringBuilder htmlBuilder = new StringBuilder("<html><title>Mail</title><body>");
        htmlBuilder.append("<table width=270>");
        int index = 0;
        for (String name : mailNicks.get(activeChar.objectId())) {
            if (index % 3 == 0) {
                if (index > 0)
                    htmlBuilder.append("</tr>");
                htmlBuilder.append("<tr>");
            }
            htmlBuilder.append("<td width=90><center>").append(name).append("</center></td>");
            index++;
        }
        htmlBuilder.append("</table></html>");//TODO end <tr>

        msg.setHtml(htmlBuilder.toString());
        activeChar.sendPacket(msg);
    }

    public static List<String> getMailNicks(Integer gmObjectId) {
        return mailNicks.get(gmObjectId);
    }

    public static void clearNicks(Integer gmObjectId) {
        mailNicks.get(gmObjectId).clear();
    }

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        GameObject target = activeChar.getTarget();
        if (!activeChar.getPlayerAccess().CanAnnounce)
            return false;

        switch (comm) {
            case "admin_add_mail":
                String targetToAdd;
                if (wordList.length > 1) {
                    targetToAdd = wordList[1];
                } else if (target instanceof Player) {
                    targetToAdd = target.getName();
                } else {
                    activeChar.sendMessage("Target a getPlayer and use //add_mail or use //add_mail nick");
                    return false;
                }

                List<String> nicks = mailNicks.containsKey(activeChar.objectId()) ? mailNicks.get(activeChar.objectId()) : new ArrayList<>();
                nicks.add(targetToAdd);
                mailNicks.put(activeChar.objectId(), nicks);
                activeChar.sendMessage("Player " + targetToAdd + " was added to the list!");
                showList(activeChar);
                break;
            case "admin_remove_mail":
                String targetToRemove;
                if (wordList.length > 1) {
                    targetToRemove = wordList[1];
                } else if (target instanceof Player) {
                    targetToRemove = target.getName();
                } else {
                    activeChar.sendMessage("Target a getPlayer and use //remove_mail or use //remove_mail nick");
                    return false;
                }
                List<String> currentNicks = mailNicks.containsKey(activeChar.objectId()) ? mailNicks.get(activeChar.objectId()) : new ArrayList<>();
                currentNicks.remove(targetToRemove);
                mailNicks.put(activeChar.objectId(), currentNicks);
                activeChar.sendMessage("Player " + targetToRemove + " was removed from the list!");
                showList(activeChar);
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_add_mail",
                "admin_remove_mail");
    }
}