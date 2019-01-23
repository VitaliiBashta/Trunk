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
        getSkill().getAddedSkills().forEach(as ->
                getEffected().addSkill(as.getSkill().id));
    }

    @Override
    public void onExit() {
        super.onExit();
        getSkill().getAddedSkills().forEach(as ->
                getEffected().removeSkill(as.getSkill()));
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}