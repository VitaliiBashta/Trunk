package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class EffectManaHeal extends Effect {
    private final boolean _ignoreMpEff;

    public EffectManaHeal(Env env, EffectTemplate template) {
        super(env, template);
        _ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
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

        double mp = calc();
        double newMp = Math.min(mp * 1.7, mp * (!_ignoreMpEff ? effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.);
        double addToMp = Math.max(0, Math.min(newMp, effected.calcStat(Stats.MP_LIMIT, null, null) * effected.getMaxMp() / 100. - effected.getCurrentMp()));

        effected.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));

        if (addToMp > 0)
            effected.setCurrentMp(addToMp + effected.getCurrentMp());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}