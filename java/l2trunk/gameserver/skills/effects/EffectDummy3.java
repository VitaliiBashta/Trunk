package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class EffectDummy3 extends Effect {
    public EffectDummy3(Env env, EffectTemplate template) {
        super(env, template);
    }

    public boolean checkCondition() {
        if (this.effected.isParalyzeImmune())
            return false;
        return super.checkCondition();
    }

    public void onStart() {
        Player target = (Player) getEffected();
        if (target.getTransformation() == 303) {
            return;
        }
        super.onStart();

        this.effected.startParalyzed();
        this.effected.abortAttack(true, true);
        this.effected.abortCast(true, true);
    }

    public void onExit() {
        super.onExit();
        this.effected.stopParalyzed();
    }

    public boolean onActionTime() {
        return false;
    }
}