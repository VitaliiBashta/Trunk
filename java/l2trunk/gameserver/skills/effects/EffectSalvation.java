package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectSalvation extends Effect {
    public EffectSalvation(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return getEffected().isPlayer() && super.checkCondition();
    }

    @Override
    public void onStart() {
        getEffected().setIsSalvation(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        getEffected().setIsSalvation(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}