package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.stats.Env;

public final class EffectAddSkills extends Effect {
    public EffectAddSkills(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        skill.getAddedSkills().forEach(as ->
                effected.addSkill(as.skill.id));
    }

    @Override
    public void onExit() {
        super.onExit();
        skill.getAddedSkills().forEach(as ->
                effected.removeSkill(as.skill.id));
    }

}