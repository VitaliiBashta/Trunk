package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class EffectCallSkills extends Effect {
    public EffectCallSkills(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Integer> skillIds = getTemplate().getParam().getIntegerList("skillIds");
        List<Integer> skillLevels = getTemplate().getParam().getIntegerList("skillLevels");

        for (int i = 0; i < skillIds.size(); i++) {
            Skill skill = SkillTable.INSTANCE.getInfo(skillIds.get(i), skillLevels.get(i));
            skill.getTargets(getEffector(), getEffected(), false).forEach(cha ->
                    getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skill)));
            getEffector().callSkill(skill, skill.getTargets(getEffector(), getEffected(), false), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}