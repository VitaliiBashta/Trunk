package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectInterrupt extends Effect {
    public EffectInterrupt(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!effected.isRaid())
            effected.abortCast(false, true);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}