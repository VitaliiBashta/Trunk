package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectNegateMusic extends Effect {
    public EffectNegateMusic(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onExit() {
        super.onExit();
    }

    @Override
    public boolean onActionTime() {
        effected.getEffectList().getAllEffects().stream()
                .filter(e -> e.getSkill().isMusic())
                .forEach(Effect::exit);
        return false;
    }
}