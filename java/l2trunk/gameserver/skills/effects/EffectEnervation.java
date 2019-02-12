package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectEnervation extends Effect {
    public EffectEnervation(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof NpcInstance)
            ((NpcInstance) effected).setParameter("DebuffIntention", 0.5);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof NpcInstance)
            ((NpcInstance) effected).setParameter("DebuffIntention", 1.);
    }
}