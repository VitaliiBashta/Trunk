package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class EffectDamOverTime extends Effect {
    // TODO уточнить уровни 1, 2, 9, 10, 11, 12
    private static final int[] bleed = new int[]{12, 17, 25, 34, 44, 54, 62, 67, 72, 77, 82, 87};
    private static final int[] poison = new int[]{11, 16, 24, 32, 41, 50, 58, 63, 68, 72, 77, 82};

    private final boolean _percent;

    public EffectDamOverTime(Env env, EffectTemplate template) {
        super(env, template);
        _percent = getTemplate().getParam().getBool("percent", false);
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        double damage = calc();
        if (_percent)
            damage = effected.getMaxHp() * _template._value * 0.01;
        if (damage < 2 && getStackOrder() != -1)
            switch (getEffectType()) {
                case Poison:
                    damage = poison[getStackOrder() - 1] * getPeriod() / 1000;
                    break;
                case Bleed:
                    damage = bleed[getStackOrder() - 1] * getPeriod() / 1000;
                    break;
            }

        damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, effected, getSkill());

        if (damage > effected.getCurrentHp() - 1 && !effected.isNpc()) {
            if (!getSkill().isOffensive())
                effected.sendPacket(SystemMsg.NOT_ENOUGH_HP);
            return false;
        }

        if (getSkill().getAbsorbPart() > 0)
            _effector.setCurrentHp(getSkill().getAbsorbPart() * Math.min(effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);

        effected.reduceCurrentHp(damage, _effector, getSkill(), !effected.isNpc() && effected != _effector, effected != _effector, _effector.isNpc() || effected == _effector, false, false, true, false);

        return true;
    }
}