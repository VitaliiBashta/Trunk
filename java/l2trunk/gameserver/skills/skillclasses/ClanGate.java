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
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isClanLeader()) {
            SystemMessage msg = Call.canSummonHere(player);
            if (msg == null) {
                return super.checkCondition(player, target, forceUse, dontMove, first);
            }
            player.sendPacket(msg);
            return false;

        } else {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return false;
        }

    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar instanceof Player) {
            Player player = (Player) activeChar;
            player.getClan().broadcastToOtherOnlineMembers(new SystemMessage2(SystemMsg.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED), player);

            getEffects(player, player, activateRate > 0, true);
        }

    }
}
