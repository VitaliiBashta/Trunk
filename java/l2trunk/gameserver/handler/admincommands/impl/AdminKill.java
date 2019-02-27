package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminKill implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        switch (comm) {
            case "admin_kill":
                if (wordList.length == 1)
                    handleKill(activeChar);
                else
                    handleKill(activeChar, wordList[1]);
                break;
            case "admin_damage":
                handleDamage(activeChar, toInt(wordList[1], 1));
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_kill",
                "admin_damage");
    }

    private void handleKill(Player activeChar) {
        handleKill(activeChar, null);
    }

    private void handleKill(Player activeChar, String player) {
        GameObject obj = activeChar.getTarget();
        if (player != null) {
            Player plyr = World.getPlayer(player);
            if (plyr != null)
                obj = plyr;
            else {
                int radius = Math.max(toInt(player), 100);
                activeChar.getAroundCharacters(radius, 200)
                        .filter(c -> !(c instanceof DoorInstance))
                        .forEach(c -> c.doDie(activeChar));
                activeChar.sendMessage("Killed within " + radius + " unit radius.");
                return;
            }
        }

        if (obj instanceof Creature) {
            Creature target = (Creature) obj;
            target.doDie(activeChar);
        } else
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
    }

    private void handleDamage(Player activeChar, int damage) {
        GameObject obj = activeChar.getTarget();

        if (obj == null) {
            activeChar.sendPacket(SystemMsg.SELECT_TARGET);
            return;
        }

        if (obj instanceof Creature) {
            Creature cha = (Creature) obj;
            cha.reduceCurrentHp(damage, activeChar, null, true, true, false, false, false, false, true);
            activeChar.sendMessage("You gave " + damage + " damage to " + cha.getName() + ".");
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }

    }

}