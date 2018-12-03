package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class EffectHealPercent extends Effect {
    private final boolean _ignoreHpEff;

    public EffectHealPercent(Env env, EffectTemplate template) {
        super(env, template);
        _ignoreHpEff = template.getParam().getBool("ignoreHpEff", true);
    }

    @Override
    public boolean checkCondition() {
        if (effected.isHealBlocked())
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isHealBlocked())
            return;

        double hp = calc() * effected.getMaxHp() / 100.;
        double newHp = hp * (!_ignoreHpEff ? effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        double addToHp = Math.max(0, Math.min(newHp, effected.calcStat(Stats.HP_LIMIT, null, null) * effected.getMaxHp() / 100. - effected.getCurrentHp()));

        effected.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));

        if (addToHp > 0)
            effected.setCurrentHp(addToHp + effected.getCurrentHp(), false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}