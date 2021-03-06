package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminRes implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Res)
            return false;

        if (fullString.startsWith("admin_res "))
            handleRes(activeChar, wordList[1]);
        if ("admin_res".equals(fullString))
            handleRes(activeChar);

        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_res";
    }

    private void handleRes(Player activeChar) {
        handleRes(activeChar, null);
    }

    private void handleRes(Player activeChar, String player) {
        GameObject obj = activeChar.getTarget();
        if (player != null) {
            Player plyr = World.getPlayer(player);
            if (plyr != null)
                obj = plyr;
            else
                try {
                    int radius = Math.max(toInt(player), 100);
                    activeChar.getAroundCharacters(radius, radius)
                    .forEach(this::handleRes);
                    activeChar.sendMessage("Resurrected within " + radius + " unit radius.");
                    return;
                } catch (NumberFormatException e) {
                    activeChar.sendMessage("Enter valid getPlayer name or radius");
                    return;
                }
        }

        if (obj == null)
            obj = activeChar;

        if (obj instanceof Creature)
            handleRes((Creature) obj);
        else
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
    }

    private void handleRes(Creature target) {
        if (!target.isDead())
            return;

        if (target instanceof Playable) {
            if (target instanceof Player)
                ((Player) target).doRevive(100.);
            else
                ((Playable) target).doRevive();
        } else if (target instanceof NpcInstance)
            ((NpcInstance) target).stopDecay();

        target.setFullHpMp();
        target.setFullCp();
    }


}