package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectAgathionRes extends Effect {
    public EffectAgathionRes(Env env, EffectTemplate template) {
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

    @Override
    public boolean onActionTime() {
        return false;
    }
}