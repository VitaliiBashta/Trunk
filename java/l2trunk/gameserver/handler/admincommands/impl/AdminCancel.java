package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import static l2trunk.commons.lang.NumberUtils.toInt;

public class AdminCancel implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(@SuppressWarnings("rawtypes") Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        switch (command) {
            case admin_cancel:
                handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
                break;
            case admin_cleanse:
                Creature target = activeChar.getTarget() != null && activeChar.getTarget().isPlayable() ? (Creature) activeChar.getTarget() : activeChar;
                target.getEffectList().getAllEffects()
                        .filter(Effect::isOffensive)
                        .filter(Effect::isCancelable)
                        .forEach(Effect::exit);
                activeChar.sendMessage("Negative effects of " + target.getName() + " were removed!");
                break;
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleCancel(Player activeChar, String targetName) {
        GameObject obj = activeChar.getTarget();
        if (targetName != null) {
            Player plyr = World.getPlayer(targetName);
            if (plyr != null)
                obj = plyr;
            else
                try {
                    int radius = Math.max(toInt(targetName), 100);
                    activeChar.getAroundCharacters(radius, 200)
                            .forEach(c -> c.getEffectList().stopAllEffects());
                    activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
                    return;
                } catch (NumberFormatException e) {
                    activeChar.sendMessage("Enter valid player name or radius");
                    return;
                }
        }

        if (obj == null)
            obj = activeChar;
        if (obj.isCreature())
            ((Creature) obj).getEffectList().stopAllEffects();
        else
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
    }

    private enum Commands {
        admin_cancel,
        admin_cleanse
    }
}