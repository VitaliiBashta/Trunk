package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.mapregion.RestartArea;

import java.util.Collections;
import java.util.List;

public final class Loc implements IUserCommandHandler {
    private static final int COMMAND_ID = 0;

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_ID != id)
            return false;

        RestartArea ra = MapRegionHolder.getInstance().getRegionData(RestartArea.class, activeChar);
        int msgId = ra != null ? ra.getRestartPoint().get(activeChar.getRace()).msgId : 0;
        if (msgId > 0)
            activeChar.sendPacket(new SystemMessage2(SystemMsg.valueOf(msgId)).addInteger(activeChar.getX()).addInteger(activeChar.getY()).addInteger(activeChar.getZ()));
        else
            activeChar.sendMessage("Current location : " + activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ());

        return true;
    }

    @Override
    public final List<Integer> getUserCommandList() {
        return List.of(COMMAND_ID);
    }
}