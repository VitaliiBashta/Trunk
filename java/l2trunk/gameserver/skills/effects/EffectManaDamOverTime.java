package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public class EffectManaDamOverTime extends Effect {
    public EffectManaDamOverTime(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        double manaDam = calc();
        if (manaDam > effected.getCurrentMp() && getSkill().isToggle()) {
            effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
            return false;
        }

        effected.reduceCurrentMp(manaDam, null);
        return true;
    }
}