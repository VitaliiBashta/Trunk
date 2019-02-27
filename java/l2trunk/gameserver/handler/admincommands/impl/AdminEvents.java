package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminEvents implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().IsEventGm)
            return false;

        switch (comm) {
            case "admin_events":
                if (wordList.length == 1)
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/events.htm"));
                else
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/" + wordList[1].trim()));
                break;
            case "admin_start_event":
                int id = toInt(wordList[1]);
                activeChar.sendMessage("Event Started!");
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_events",
                "admin_start_event");
    }
}