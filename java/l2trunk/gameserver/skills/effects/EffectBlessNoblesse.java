package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectBlessNoblesse extends Effect {
    public EffectBlessNoblesse(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.setIsBlessedByNoblesse(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.setIsBlessedByNoblesse(false);
    }

}