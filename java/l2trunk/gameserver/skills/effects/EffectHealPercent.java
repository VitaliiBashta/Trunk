package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectHealPercent extends Effect {
    private final boolean ignoreHpEff;

    public EffectHealPercent(Env env, EffectTemplate template) {
        super(env, template);
        ignoreHpEff = template.getParam().isSet("ignoreHpEff");
    }

    @Override
    public boolean checkCondition() {
        return !effected.isHealBlocked();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isHealBlocked())
            return;

        double hp = calc() * effected.getMaxHp() / 100.;
        double newHp = hp * (!ignoreHpEff ? effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., effector, skill) : 100.) / 100.;
        double addToHp = Math.max(0, Math.min(newHp, effected.calcStat(Stats.HP_LIMIT, null, null) * effected.getMaxHp() / 100. - effected.getCurrentHp()));

        effected.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));

        if (addToHp > 0)
            effected.setCurrentHp(addToHp + effected.getCurrentHp(), false);
    }
}