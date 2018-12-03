package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectMPDamPercent extends Effect {
    public EffectMPDamPercent(final Env env, final EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isDead())
            return;

        double newMp = (100. - calc()) * effected.getMaxMp() / 100.;
        newMp = Math.min(effected.getCurrentMp(), Math.max(0, newMp));
        effected.setCurrentMp(newMp);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}