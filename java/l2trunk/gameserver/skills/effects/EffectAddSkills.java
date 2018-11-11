package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill.AddedSkill;
import l2trunk.gameserver.stats.Env;

public class EffectAddSkills extends Effect {
    public EffectAddSkills(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        for (AddedSkill as : getSkill().getAddedSkills())
            getEffected().addSkill(as.getSkill());
    }

    @Override
    public void onExit() {
        super.onExit();
        for (AddedSkill as : getSkill().getAddedSkills())
            getEffected().removeSkill(as.getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}