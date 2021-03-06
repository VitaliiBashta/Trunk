package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

public final class DeathPenalty extends Skill {
    public DeathPenalty(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final Player player, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        // Chaotic characters can't use scrolls of recovery
        if (player != null)
            if (player.getKarma() > 0 && !Config.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY) {
                player.sendActionFailed();
                return false;
            } else return false;
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target instanceof Player) {
            ((Player) target).getDeathPenalty().reduceLevel();
        }
    }
}