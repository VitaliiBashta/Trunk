package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectSummonManaHealPercent extends Effect {
    private final boolean ignoreMpEff;

    public EffectSummonManaHealPercent(Env env, EffectTemplate template) {
        super(env, template);
        ignoreMpEff = template.getParam().isSet("ignoreMpEff");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isHealBlocked())
            return;

        double mp = calc() * effected.getMaxMp() / 100.;
        double newMp = mp * (!ignoreMpEff ? effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., effector, skill) : 100.) / 100.;
        double addToMp = Math.max(0, Math.min(newMp, effected.calcStat(Stats.MP_LIMIT, null, null) * effected.getMaxMp() / 100. - effected.getCurrentMp()));

        effected.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));

        if (addToMp > 0)
            effected.setCurrentMp(addToMp + effected.getCurrentMp());
    }

}