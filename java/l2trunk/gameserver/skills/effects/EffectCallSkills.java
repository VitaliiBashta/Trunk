package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.tables.SkillTable;

public class EffectCallSkills extends Effect {
    public EffectCallSkills(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        int[] skillIds = getTemplate().getParam().getIntegerArray("skillIds");
        int[] skillLevels = getTemplate().getParam().getIntegerArray("skillLevels");

        for (int i = 0; i < skillIds.length; i++) {
            Skill skill = SkillTable.INSTANCE().getInfo(skillIds[i], skillLevels[i]);
            for (Creature cha : skill.getTargets(getEffector(), getEffected(), false))
                getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skillIds[i], skillLevels[i], 0, 0));
            getEffector().callSkill(skill, skill.getTargets(getEffector(), getEffected(), false), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}