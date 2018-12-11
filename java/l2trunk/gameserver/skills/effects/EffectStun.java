package l2trunk.gameserver.skills.effects;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectStun extends Effect {
    public EffectStun(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return Rnd.chance(template.chance(80));
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startStunning();
        effected.abortAttack(true, true);
        effected.abortCast(true, true);
        effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopStunning();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}