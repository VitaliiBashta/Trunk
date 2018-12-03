package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectHPDamPercent extends Effect {
    public EffectHPDamPercent(final Env env, final EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected.isDead())
            return;

        double newHp = (100. - calc()) * effected.getMaxHp() / 100.;
        newHp = Math.min(effected.getCurrentHp(), Math.max(0, newHp));
        effected.setCurrentHp(newHp, false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}