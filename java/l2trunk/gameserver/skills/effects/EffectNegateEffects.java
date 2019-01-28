package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectNegateEffects extends Effect {
    public EffectNegateEffects(Env env, EffectTemplate template) {
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
                .filter(e -> !e.getStackType().equals(EffectTemplate.NO_STACK))
                .filter(e -> (e.getStackType().equals(getStackType())
                        || e.getStackType().equals(getStackType2()))
                        || !e.getStackType2().equals(EffectTemplate.NO_STACK))
                .filter(e -> e.getStackType2().equals(getStackType()) || e.getStackType2().equals(getStackType2()))
                .filter(e -> e.getStackOrder() <= getStackOrder())
                .forEach(Effect::exit);
        return false;
    }
}