package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectDebuffImmunity extends Effect {
    public EffectDebuffImmunity(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startDebuffImmunity();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopDebuffImmunity();
    }

}