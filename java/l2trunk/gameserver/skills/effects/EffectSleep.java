package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectSleep extends Effect {
    public EffectSleep(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startSleeping();
        effected.abortAttack(true, true);
        effected.abortCast(true, true);
        effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopSleeping();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}