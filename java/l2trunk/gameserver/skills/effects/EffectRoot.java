package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectRoot extends Effect {
    public EffectRoot(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startRooted();
        _effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopRooted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}