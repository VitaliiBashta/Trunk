package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectDamOverTimeLethal extends Effect {
    public EffectDamOverTimeLethal(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        double damage = calc();

        if (skill.isOffensive)
            damage *= 2;

        damage = effector.calcStat(skill.isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, effected, skill);

        effected.reduceCurrentHp(damage, effector, skill, !(effected instanceof NpcInstance) && effected != effector, effected != effector, effector instanceof NpcInstance || effected == effector, false, false, true, false);

        return true;
    }
}