package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectMeditation extends Effect {
    public EffectMeditation(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startParalyzed();
        effected.setMeditated(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopParalyzed();
        effected.setMeditated(false);
    }

}