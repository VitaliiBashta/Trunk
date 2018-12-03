package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectMuteAll extends Effect {
    public EffectMuteAll(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startMuted();
        effected.startPMuted();
        effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopMuted();
        effected.stopPMuted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}