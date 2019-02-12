package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectConsumeSoulsOverTime extends Effect {
    public EffectConsumeSoulsOverTime(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        if (effected.getConsumedSouls() < 0)
            return false;

        int damage = (int) calc();

        if (effected.getConsumedSouls() < damage)
            effected.setConsumedSouls(0, null);
        else
            effected.setConsumedSouls(effected.getConsumedSouls() - damage, null);

        return true;
    }
}