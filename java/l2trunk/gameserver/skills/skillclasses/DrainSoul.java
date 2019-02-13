package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class DrainSoul extends Skill {
    public DrainSoul(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target instanceof MonsterInstance)
            return super.checkCondition(player, target, forceUse, dontMove, first);
        player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
        return false;
    }

    @Override
    public void useSkill(Creature activeChar, Creature targets) {
        // This is just a dummy skill for the soul crystal skill condition,
        // since the Soul Crystal item handler already does everything.
    }
}
