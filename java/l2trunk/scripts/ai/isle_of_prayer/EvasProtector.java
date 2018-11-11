package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.instances.CrystalCaverns;

public class EvasProtector extends DefaultAI {
    public EvasProtector(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();

        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            if (skill.getSkillType() == Skill.SkillType.HEAL)
                refl.notifyProtectorHealed(actor);
        super.onEvtSeeSpell(skill, caster);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}