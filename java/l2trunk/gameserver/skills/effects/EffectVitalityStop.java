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
        Player player = effected.getPlayer();
        player.VitalityStop(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        Player player = effected.getPlayer();
        player.VitalityStop(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}