package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.stats.Formulas;

public final class DeleteHateOfMe extends Skill {
    public DeleteHateOfMe(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target != null) {
            if (activeChar instanceof Player && ((Player) activeChar).isGM())
                ((Player)activeChar).sendMessage(new CustomMessage("l2trunk.gameserver.skills.Formulas.Chance").addString(name).addNumber(activateRate));

            if (target instanceof NpcInstance && Formulas.calcSkillSuccess(activeChar, target, this, activateRate)) {
                NpcInstance npc = (NpcInstance) target;
                npc.getAggroList().remove(activeChar, true);
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
            getEffects(activeChar, target, true, false);
        }
    }
}