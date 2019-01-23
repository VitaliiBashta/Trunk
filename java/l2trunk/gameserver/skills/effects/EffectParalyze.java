package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.stats.Env;

public final class EffectParalyze extends Effect {
    public EffectParalyze(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (effected.isParalyzeImmune())
            return false;
        if (effector.getPet() != null && effected == effector.getPet()) {
            effector.getPlayer().sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
            return false;
        }

        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startParalyzed();
        effected.abortAttack(true, true);
        effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopParalyzed();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}