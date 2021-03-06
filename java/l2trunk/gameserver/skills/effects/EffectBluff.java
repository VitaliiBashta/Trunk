package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.FinishRotating;
import l2trunk.gameserver.network.serverpackets.StartRotating;
import l2trunk.gameserver.stats.Env;

public final class EffectBluff extends Effect {
    public EffectBluff(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return !(effected instanceof NpcInstance) || effected instanceof MonsterInstance;
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

}