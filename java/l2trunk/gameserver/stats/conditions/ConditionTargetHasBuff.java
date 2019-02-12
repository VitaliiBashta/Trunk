package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetHasBuff extends Condition {
    private final EffectType effecttype;
    private final int level;

    public ConditionTargetHasBuff(EffectType effectType, int level) {
        effecttype = effectType;
        this.level = level;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target == null)
            return false;
        Effect effect = target.getEffectList().getEffectByType(effecttype);
        if (effect == null)
            return false;
        return level == -1 || effect.skill.level >= level;
    }
}
