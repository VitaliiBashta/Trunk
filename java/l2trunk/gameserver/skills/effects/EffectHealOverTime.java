package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExRegenMax;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectHealOverTime extends Effect {
    private final boolean ignoreHpEff;

    public EffectHealOverTime(Env env, EffectTemplate template) {
        super(env, template);
        ignoreHpEff = template.getParam().isSet("ignoreHpEff");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected instanceof Player && getCount() > 0 && getPeriod() > 0)
            effected.sendPacket(new ExRegenMax(calc(), (int) (getCount() * getPeriod() / 1000),(int) Math.round(getPeriod() / 1000.)));
    }

    @Override
    public boolean onActionTime() {
        if (effected.isHealBlocked())
            return true;

        double hp = calc();
        double newHp = hp * (!ignoreHpEff ? effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., effector, skill) : 100.) / 100.;
        double addToHp = Math.max(0, Math.min(newHp, effected.calcStat(Stats.HP_LIMIT, null, null) * effected.getMaxHp() / 100. - effected.getCurrentHp()));

        if (addToHp > 0)
            effected.setCurrentHp(effected.getCurrentHp() + addToHp, false);

        return true;
    }
}