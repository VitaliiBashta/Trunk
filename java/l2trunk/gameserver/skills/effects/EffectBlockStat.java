package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.skills.skillclasses.NegateStats;
import l2trunk.gameserver.stats.Env;

public final class EffectBlockStat extends Effect {
    public EffectBlockStat(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.addBlockStats(((NegateStats) skill).getNegateStats());
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.removeBlockStats(((NegateStats) skill).getNegateStats());
    }

}