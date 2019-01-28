package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectRemoveTarget extends Effect {
    private final boolean _doStopTarget;

    public EffectRemoveTarget(Env env, EffectTemplate template) {
        super(env, template);
        _doStopTarget = template.getParam().getBool("doStopTarget", false);
    }

    @Override
    public void onStart() {
        if ((effected.getAI() instanceof DefaultAI)) {
            ((DefaultAI) effected.getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
        }
        effected.setTarget(null);
        if (_doStopTarget) {
            effected.stopMove();
        }
        effected.abortAttack(true, true);
        effected.abortCast(true, true);
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
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