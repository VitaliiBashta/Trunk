package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

import java.util.List;
import java.util.Objects;

public final class DeathPenalty extends Skill {
    public DeathPenalty(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        // Chaotic characters can't use scrolls of recovery
        if (activeChar.getKarma() > 0 && !Config.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY) {
            activeChar.sendActionFailed();
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.stream()
                .filter(Objects::nonNull)
                .filter(GameObject::isPlayer)
                .map(t -> (Player) t)
                .forEach(p -> p.getDeathPenalty().reduceLevel());
    }
}