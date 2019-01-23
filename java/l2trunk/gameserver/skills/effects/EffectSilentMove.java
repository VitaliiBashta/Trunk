package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public final class EffectSilentMove extends Effect {
    public EffectSilentMove(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected.isPlayable())
            ((Playable) effected).startSilentMoving();
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected.isPlayable())
            ((Playable) effected).stopSilentMoving();
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        if (!getSkill().isToggle())
            return false;

        double manaDam = calc();
        if (manaDam > effected.getCurrentMp()) {
            effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().id, getSkill().getDisplayLevel()));
            return false;
        }

        effected.reduceCurrentMp(manaDam, null);
        return true;
    }
}