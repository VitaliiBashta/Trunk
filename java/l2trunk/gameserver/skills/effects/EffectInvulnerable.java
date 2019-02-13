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
        return skill == null || (skill.skillType != SkillType.TAKECASTLE && skill.skillType != SkillType.TAKEFORTRESS && skill.skillType != SkillType.TAKEFLAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        effected.startHealBlocked();
        effected.setInvul(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopHealBlocked();
        effected.setInvul(false);
    }

}