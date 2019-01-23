package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public class EffectHate extends Effect {
    public EffectHate(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected.isMonster()) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, effector,(int) template._value);
        }
        // On players it makes attack the caster
        else if (effected.isPlayable() && effected.isMonster()) {
            getEffected().abortAttack(true, false);
            getEffected().getAI().setIntentionAttack(getEffector());
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}