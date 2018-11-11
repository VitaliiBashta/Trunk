package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class BuffCharger extends Skill {
    private final int _target;

    public BuffCharger(StatsSet set) {
        super(set);
        _target = set.getInteger("targetBuff", 0);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            int level = 0;
            List<Effect> el = target.getEffectList().getEffectsBySkillId(_target);
            if (el != null)
                level = el.get(0).getSkill().getLevel();

            Skill next = SkillTable.getInstance().getInfo(_target, level + 1);
            if (next != null)
                next.getEffects(activeChar, target, false, false);
        }
    }
}