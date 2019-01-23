package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class EffectHeal extends Effect {
    private final boolean _ignoreHpEff;

    public EffectHeal(Env env, EffectTemplate template) {
        super(env, template);
        _ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
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

        double hp = calc();
        double newHp = hp * (!_ignoreHpEff ? effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., effector, getSkill()) : 100.) / 100.;
        double addToHp = Math.max(0, Math.min(newHp, effected.calcStat(Stats.HP_LIMIT, null, null) * effected.getMaxHp() / 100. - effected.getCurrentHp()));

        if (addToHp > 0) {
            effected.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
            effected.setCurrentHp(addToHp + effected.getCurrentHp(), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}