package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectDestroySummon extends Effect {
    public EffectDestroySummon(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return effected instanceof SummonInstance;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Summon) effected).unSummon();
    }

}