package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectSalvation extends Effect {
    public EffectSalvation(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return effected instanceof Player;
    }

    @Override
    public void onStart() {
        effected.setIsSalvation(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.setIsSalvation(false);
    }

}