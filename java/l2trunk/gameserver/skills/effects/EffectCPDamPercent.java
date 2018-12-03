package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectCPDamPercent extends Effect {
    public EffectCPDamPercent(final Env env, final EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isDead())
            return;

        double newCp = (100. - calc()) * effected.getMaxCp() / 100.;
        newCp = Math.min(effected.getCurrentCp(), Math.max(0, newCp));
        effected.setCurrentCp(newCp);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}