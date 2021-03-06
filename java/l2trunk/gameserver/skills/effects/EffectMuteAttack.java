package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectMuteAttack extends Effect {
    public EffectMuteAttack(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!effected.startAMuted()) {
            effected.abortCast(true, true);
            effected.abortAttack(true, true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopAMuted();
    }

}