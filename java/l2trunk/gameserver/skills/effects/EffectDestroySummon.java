package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.stats.Env;

public final class EffectDestroySummon extends Effect {
    public EffectDestroySummon(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (!effected.isSummon())
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Summon) effected).unSummon();
    }

    @Override
    public boolean onActionTime() {
        // just stop this effect
        return false;
    }
}