package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class DefuseTrap extends Skill {
    public DefuseTrap(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target instanceof TrapInstance) {
            return super.checkCondition(player, target, forceUse, dontMove, first);
        }
        player.sendPacket(SystemMsg.INVALID_TARGET);
        return false;
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target instanceof TrapInstance) {
            TrapInstance trap = (TrapInstance) target;
            if (trap.getLevel() <= power)
                trap.deleteMe();
        }
    }
}