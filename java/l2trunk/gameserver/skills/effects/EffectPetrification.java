package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectPetrification extends Effect {
    public EffectPetrification(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return !effected.isParalyzeImmune();
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startParalyzed();
        effected.startDebuffImmunity();
        effected.startBuffImmunity();
        effected.startDamageBlocked();
        effected.abortAttack(true, true);
        effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopParalyzed();
        effected.stopDebuffImmunity();
        effected.stopBuffImmunity();
        effected.stopDamageBlocked();
    }

}