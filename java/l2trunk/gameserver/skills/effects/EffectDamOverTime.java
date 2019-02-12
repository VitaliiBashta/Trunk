package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class EffectDamOverTime extends Effect {
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
            damage = effected.getMaxHp() * template.value * 0.01;
        if (damage < 2 && getStackOrder() != -1)
            switch (getEffectType()) {
                case Poison:
                    damage = poison[getStackOrder() - 1] * getPeriod() / 1000.;
                    break;
                case Bleed:
                    damage = bleed[getStackOrder() - 1] * getPeriod() / 1000.;
                    break;
            }

        damage = effector.calcStat(skill.isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, effected, skill);

        if (damage > effected.getCurrentHp() - 1 && !(effected instanceof NpcInstance)) {
            if (!skill.isOffensive)
                effected.sendPacket(SystemMsg.NOT_ENOUGH_HP);
            return false;
        }

        if (skill.absorbPart > 0)
            effector.setCurrentHp(skill.absorbPart * Math.min(effected.getCurrentHp(), damage) + effector.getCurrentHp(), false);

        effected.reduceCurrentHp(damage, effector, skill, !(effected instanceof NpcInstance) && effected != effector, effected != effector, effector instanceof NpcInstance || effected == effector, false, false, true, false);

        return true;
    }
}