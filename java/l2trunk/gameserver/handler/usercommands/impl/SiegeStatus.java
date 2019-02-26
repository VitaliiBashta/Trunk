package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Collections;
import java.util.List;

public final class SiegeStatus implements IUserCommandHandler {
    private static final int COMMANDS = 99;

    @Override
    public boolean useUserCommand(int id, Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
            return false;
        }

        Castle castle = player.getCastle();
        if (castle == null)
            return false;

        if (castle.getSiegeEvent().isInProgress())
            if (!player.isNoble()) {
                player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
                return false;
            }

        NpcHtmlMessage msg = new NpcHtmlMessage(5);
        msg.setFile("siege_status.htm");
        msg.replace("%name%", player.getName());
        msg.replace("%kills%", 0);
        msg.replace("%deaths%", 0);
        msg.replace("%type%", 0);

        player.sendPacket(msg);
        return true;
    }

    @Override
    public List<Integer> getUserCommandList() {
        return List.of(COMMANDS);
    }
}
