package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectVitalityStop extends Effect {
    public EffectVitalityStop(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof Player)
            ((Player)effected).VitalityStop(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof Player)
            ((Player)effected).VitalityStop(false);
    }

}