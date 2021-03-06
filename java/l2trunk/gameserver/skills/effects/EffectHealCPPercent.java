package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectHealCPPercent extends Effect {
    private final boolean ignoreCpEff;

    public EffectHealCPPercent(Env env, EffectTemplate template) {
        super(env, template);
        ignoreCpEff = template.getParam().isSet("ignoreCpEff");
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

        double cp = calc() * effected.getMaxCp() / 100.;
        double newCp = cp * (!ignoreCpEff ? effected.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., effector, skill) : 100.) / 100.;
        double addToCp = Math.max(0, Math.min(newCp, effected.calcStat(Stats.CP_LIMIT, null, null) * effected.getMaxCp() / 100. - effected.getCurrentCp()));

        effected.sendPacket(new SystemMessage2(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger((long) addToCp));

            effected.addCp(addToCp);
    }

}