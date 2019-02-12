package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Collections;
import java.util.List;

/**
 * Support for /olympiadstat command
 */
public final class OlympiadStat implements IUserCommandHandler {
    private static final int COMMAND_IDS = 109;

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (id != COMMAND_IDS)
            return false;

        GameObject objectTarget = Config.OLYMPIAD_OLDSTYLE_STAT ? activeChar : activeChar.getTarget();
        if (!(objectTarget instanceof Player) || !((Player)objectTarget).isNoble()) {
            activeChar.sendPacket(SystemMsg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
            return true;
        }

        Player playerTarget = (Player)objectTarget;

        SystemMessage2 sm = new SystemMessage2(SystemMsg.FOR_THE_CURRENT_GRAND_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINTS);
        sm.addInteger(Olympiad.getCompetitionDone(playerTarget.objectId()));
        sm.addInteger(Olympiad.getCompetitionWin(playerTarget.objectId()));
        sm.addInteger(Olympiad.getCompetitionLoose(playerTarget.objectId()));
        sm.addInteger(Olympiad.getNoblePoints(playerTarget.objectId()));

        activeChar.sendPacket(sm);

        int[] ar = Olympiad.getWeekGameCounts(playerTarget.objectId());
        sm = new SystemMessage2(SystemMsg.YOU_HAVE_S1_MATCHES_REMAINING_THAT_YOU_CAN_PARTICIPATE_IN_THIS_WEEK_S2_1_VS_1_CLASS_MATCHES_S3_1_VS_1_MATCHES__S4_3_VS_3_TEAM_MATCHES);
        sm.addInteger(ar[0]);
        sm.addInteger(ar[1]);
        sm.addInteger(ar[2]);
        sm.addInteger(ar[3]);
        activeChar.sendPacket(sm);
        return true;
    }

    @Override
    public List<Integer> getUserCommandList() {
        return Collections.singletonList(COMMAND_IDS);
    }
}