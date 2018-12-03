package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
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

        if (getSkill().isOffensive())
            damage *= 2;

        damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, effected, getSkill());

        effected.reduceCurrentHp(damage, _effector, getSkill(), !effected.isNpc() && effected != _effector, effected != _effector, _effector.isNpc() || effected == _effector, false, false, true, false);

        return true;
    }
}