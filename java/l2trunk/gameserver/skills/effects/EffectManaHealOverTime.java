package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class EffectManaHealOverTime extends Effect {
    private final boolean ignoreMpEff;

    public EffectManaHealOverTime(Env env, EffectTemplate template) {
        super(env, template);
        ignoreMpEff = template.getParam().isSet("ignoreMpEff");
    }

    @Override
    public boolean onActionTime() {
        if (effected.isHealBlocked())
            return true;

        double mp = calc();
        double newMp = mp * (!ignoreMpEff ? effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., effector, skill) : 100.) / 100.;
        double addToMp = Math.max(0, Math.min(newMp, effected.calcStat(Stats.MP_LIMIT, null, null) * effected.getMaxMp() / 100. - effected.getCurrentMp()));

        if (addToMp > 0)
            effected.setCurrentMp(effected.getCurrentMp() + addToMp);

        return true;
    }
}