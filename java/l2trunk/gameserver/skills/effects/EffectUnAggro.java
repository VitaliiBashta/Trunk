package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectUnAggro extends Effect {
    public EffectUnAggro(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof NpcInstance)
            ((NpcInstance) effected).setUnAggred(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof NpcInstance)
            ((NpcInstance) effected).setUnAggred(false);
    }

}