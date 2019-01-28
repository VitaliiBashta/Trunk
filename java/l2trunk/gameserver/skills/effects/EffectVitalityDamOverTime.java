package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.stats.Env;

public final class EffectVitalityDamOverTime extends Effect {
    public EffectVitalityDamOverTime(Env env, EffectTemplate template) {
        super(env, template);
    }

    public boolean onActionTime() {
        if ((this.effected.isDead()) || (!this.effected.isPlayer())) {
            return false;
        }
        Player pEffected = (Player) this.effected;

        double vitDam = calc();
        if ((vitDam > pEffected.getVitality()) && (getSkill().isToggle())) {
            pEffected.sendPacket(Msg.NOT_ENOUGH_MATERIALS);
            pEffected.sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().id, getSkill().getDisplayLevel()));
            return false;
        }

        pEffected.setVitality(Math.max(0.0D, pEffected.getVitality() - vitDam));
        return true;
    }
}