package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminHeal implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().Heal)
            return false;

        if (wordList.length == 1)
            handleRes(activeChar);
        else
            handleRes(activeChar, wordList[1]);

        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_heal";
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
            else {
                int radius = Math.max(toInt(player), 100);
                activeChar.getAroundCharacters(radius, 200)
                        .peek(Creature::setFullHpMp)
                        .forEach(Creature::setFullCp);
                activeChar.sendMessage("Healed within " + radius + " unit radius.");
                return;
            }
        }

        if (obj == null)
            obj = activeChar;

        if (obj instanceof Creature) {
            Creature target = (Creature) obj;
            target.setFullHpMp();
            if (target instanceof Player)
                target.setFullCp();
        } else
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
    }

}