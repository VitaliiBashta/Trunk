package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public class EffectRelax extends Effect {
    private boolean _isWereSitting;

    public EffectRelax(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        Player player = effected.getPlayer();
        if (player == null)
            return false;
        if (player.isMounted()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.id, skill.level));
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = effected.getPlayer();
        if (player.isMoving)
            player.stopMove();
        _isWereSitting = player.isSitting();
        player.sitDown(null);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (!_isWereSitting)
            effected.getPlayer().standUp();
    }

    @Override
    public boolean onActionTime() {
        Player player = effected.getPlayer();
        if (player.isAlikeDead() || player == null)
            return false;

        if (!player.isSitting())
            return false;

        if (player.isCurrentHpFull() && getSkill().isToggle()) {
            getEffected().sendPacket(SystemMsg.THAT_SKILL_HAS_BEEN_DEACTIVATED_AS_HP_WAS_FULLY_RECOVERED);
            return false;
        }

        double manaDam = calc();
        if (manaDam > effected.getCurrentMp())
            if (getSkill().isToggle()) {
                player.sendPacket(SystemMsg.NOT_ENOUGH_MP, new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().id, getSkill().getDisplayLevel()));
                return false;
            }

        effected.reduceCurrentMp(manaDam, null);

        return true;
    }
}