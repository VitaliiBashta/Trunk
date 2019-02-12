package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

public final class SPHeal extends Skill {
    public SPHeal(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final Player activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addExpAndSp(0, (long) power);
            getEffects(activeChar, player, activateRate > 0, false);
        }
    }
}
