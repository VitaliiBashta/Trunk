package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.stats.Env;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public final class EffectBetray extends Effect {
    public EffectBetray(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof Summon) {
            Summon summon = (Summon) effected;
            summon.setDepressed(true);
            summon.getAI().Attack(summon.owner, true, false);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof Summon) {
            Summon summon = (Summon) effected;
            summon.setDepressed(false);
            summon.getAI().setIntention(AI_INTENTION_ACTIVE);
        }
    }

}