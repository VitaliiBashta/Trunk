package l2trunk.gameserver.handler.admincommands.impl;


import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.model.Player;

import java.util.List;

public final class AdminHellbound implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Menu)
            return false;

        switch (comm) {
            case "admin_hbadd":
                HellboundManager.addConfidence(Long.parseLong(wordList[1]));
                activeChar.sendMessage("Added " + NumberUtils.toInt(wordList[1], 1) + " to Hellbound confidence");
                activeChar.sendMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
            case "admin_hbsub":
                HellboundManager.reduceConfidence(Long.parseLong(wordList[1]));
                activeChar.sendMessage("Reduced confidence by " + NumberUtils.toInt(wordList[1], 1));
                activeChar.sendMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
            case "admin_hbset":
                HellboundManager.setConfidence(Long.parseLong(wordList[1]));
                activeChar.sendMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_hbadd",
                "admin_hbsub",
                "admin_hbset");
    }

}