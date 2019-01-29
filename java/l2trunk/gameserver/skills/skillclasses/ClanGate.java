package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class ClanGate extends Skill {
    public ClanGate(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!activeChar.isPlayer())
            return false;

        Player player = (Player) activeChar;
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return false;
        }

        SystemMessage msg = Call.canSummonHere(player);
        if (msg != null) {
            activeChar.sendPacket(msg);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer())
            return;

        Player player = (Player) activeChar;
        player.getClan().broadcastToOtherOnlineMembers(new SystemMessage2(SystemMsg.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED), player);

        getEffects(activeChar, activeChar, activateRate > 0, true);
    }
}
