package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminDoorControl implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Door)
            return false;

        GameObject target;

        switch (comm) {
            case "admin_open":
                if (wordList.length > 1)
                    target = World.getAroundObjectById(activeChar, toInt(wordList[1]));
                else
                    target = activeChar.getTarget();

                if (target instanceof DoorInstance)
                    ((DoorInstance) target).openMe();
                else
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);

                break;
            case "admin_close":
                if (wordList.length > 1)
                    target = World.getAroundObjectById(activeChar, toInt(wordList[1]));
                else
                    target = activeChar.getTarget();
                if (target instanceof DoorInstance)
                    ((DoorInstance) target).closeMe();
                else
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_open",
                "admin_close");
    }
}