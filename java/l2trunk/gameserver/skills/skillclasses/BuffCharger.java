package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class BuffCharger extends Skill {
    private final int target;

    public BuffCharger(StatsSet set) {
        super(set);
        target = set.getInteger("targetBuff", 0);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.forEach(target -> {
            int level = target.getEffectList().getEffectsBySkillId(this.target)
                    .map(e -> e.skill.level).findFirst().orElse(0);
            Skill next = SkillTable.INSTANCE.getInfo(this.target, level + 1);
            if (next != null)
                next.getEffects(activeChar, target);
        });
    }
}