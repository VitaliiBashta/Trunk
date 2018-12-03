package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectCombatPointHealOverTime extends Effect {
    public EffectCombatPointHealOverTime(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean onActionTime() {
        if (effected.isHealBlocked())
            return true;

        double addToCp = Math.max(0, Math.min(calc(), effected.calcStat(Stats.CP_LIMIT, null, null) * effected.getMaxCp() / 100. - effected.getCurrentCp()));
        if (addToCp > 0)
            effected.setCurrentCp(effected.getCurrentCp() + addToCp);

        return true;
    }
}