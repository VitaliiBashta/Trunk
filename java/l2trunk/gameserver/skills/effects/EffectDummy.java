package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectDummy extends Effect {
    public EffectDummy(Env env, EffectTemplate template) {
        super(env, template);
    }

    public void onStart() {
        Player target = (Player) effected;
        if (target.getTransformation() == 303) {
            return;
        }
        super.onStart();
    }

    public void onExit() {
        super.onExit();
    }

    public boolean onActionTime() {
        return false;
    }
}