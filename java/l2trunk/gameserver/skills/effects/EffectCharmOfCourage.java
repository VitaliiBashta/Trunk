package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectCharmOfCourage extends Effect {
    public EffectCharmOfCourage(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof Player)
            ((Player)effected).setCharmOfCourage(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        ((Player)effected).setCharmOfCourage(false);
    }

}