package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectHate extends Effect {
    public EffectHate(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof MonsterInstance) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, effector,(int) template.value);
        }
        // On players it makes attack the caster
        else if (effected instanceof Playable) {
            effected.abortAttack(true, false);
            effected.getAI().setIntentionAttack(effector);
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

}