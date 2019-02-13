package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectHourglass extends Effect {
    public EffectHourglass(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof Player)
            ((Player)effected).startHourglassEffect();
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof Player)
            ((Player)effected).stopHourglassEffect();
    }

}