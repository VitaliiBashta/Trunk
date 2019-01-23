package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.PlayerAI;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectAggression extends Effect {
    public EffectAggression(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected.isPlayer() && effected != effector)
            ((PlayerAI) effected.getAI()).lockTarget(effector);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected.isPlayer() && effected != effector)
            ((PlayerAI) effected.getAI()).lockTarget(null);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}