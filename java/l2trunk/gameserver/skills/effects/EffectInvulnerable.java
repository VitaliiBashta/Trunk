package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.stats.Env;

public final class EffectInvulnerable extends Effect {
    public EffectInvulnerable(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (effected.isInvul())
            return false;
        Skill skill = effected.getCastingSkill();
        if (skill != null && (skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS || skill.getSkillType() == SkillType.TAKEFLAG))
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startHealBlocked();
        effected.setIsInvul(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopHealBlocked();
        effected.setIsInvul(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}