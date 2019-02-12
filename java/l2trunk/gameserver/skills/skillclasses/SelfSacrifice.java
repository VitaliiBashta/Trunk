package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SelfSacrifice extends Skill {
    private final int effRadius;

    public SelfSacrifice(StatsSet set) {
        super(set);
        this.effRadius = set.getInteger("effRadius", 1000);
    }

    @Override
    public List<Creature> getTargets(final Creature activeChar, final Creature aimingTarget, final boolean forceUse) {
        return activeChar.getAroundCharacters(this.effRadius, 1000)
                .filter(Objects::nonNull)
                .filter(o -> o instanceof Player)
                .filter(target -> !target.isAutoAttackable(activeChar))
                .collect(Collectors.toList());
    }

    @Override
    public void useSkill(final Creature activeChar, final Creature target) {
        if (target != null && (skillType != Skill.SkillType.BUFF) || (target == activeChar)) {
            getEffects(activeChar, target, activateRate > 0, false, false);
        }
    }
}