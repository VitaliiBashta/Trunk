package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Env;

public class EffectMute extends Effect {
    public EffectMute(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!effected.startMuted()) {
            Skill castingSkill = effected.getCastingSkill();
            if (castingSkill != null && castingSkill.isMagic())
                effected.abortCast(true, true);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopMuted();
    }
}