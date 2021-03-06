package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectBuff extends Effect {
    public EffectBuff(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Ady - Hardcoded buff immunity for Day of Doom debuff (Prevents from receiving buff)
        if (skill.id == 5145)
            effected.startBuffImmunity();
    }

    @Override
    public void onExit() {
        super.onExit();

        // Ady - Hardcoded buff immunity for Day of Doom debuff (Prevents from receiving buff)
        if (skill.id == 5145)
            effected.stopBuffImmunity();
    }

}