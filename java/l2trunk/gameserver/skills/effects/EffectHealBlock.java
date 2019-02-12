package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectHealBlock extends Effect {
    public EffectHealBlock(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return !effected.isHealBlocked();
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startHealBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopHealBlocked();
    }

}