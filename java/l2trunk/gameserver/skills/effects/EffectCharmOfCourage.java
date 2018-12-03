package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectCharmOfCourage extends Effect {
    public EffectCharmOfCourage(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected.isPlayer())
            effected.getPlayer().setCharmOfCourage(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.getPlayer().setCharmOfCourage(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}