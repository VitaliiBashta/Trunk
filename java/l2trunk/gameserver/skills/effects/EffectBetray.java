package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.stats.Env;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public class EffectBetray extends Effect {
    public EffectBetray(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected != null && _effected.isSummon()) {
            Summon summon = (Summon) _effected;
            summon.setDepressed(true);
            summon.getAI().Attack(summon.getPlayer(), true, false);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected != null && _effected.isSummon()) {
            Summon summon = (Summon) _effected;
            summon.setDepressed(false);
            summon.getAI().setIntention(AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}