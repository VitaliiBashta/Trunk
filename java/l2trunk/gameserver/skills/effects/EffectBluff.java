package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.FinishRotating;
import l2trunk.gameserver.network.serverpackets.StartRotating;
import l2trunk.gameserver.stats.Env;

public final class EffectBluff extends Effect {
    public EffectBluff(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (effected.isNpc() && !effected.isMonster())
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        effected.broadcastPacket(new StartRotating(effected, effected.getHeading(), 1, 65535));
        effected.broadcastPacket(new FinishRotating(effected, effector.getHeading(), 65535));
        effected.setHeading(effector.getHeading());
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