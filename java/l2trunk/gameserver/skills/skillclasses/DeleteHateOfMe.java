package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.stats.Formulas;

import java.util.List;

public final class DeleteHateOfMe extends Skill {
    public DeleteHateOfMe(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                if (activeChar.isPlayer() && ((Player) activeChar).isGM())
                    activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.Formulas.Chance", (Player) activeChar).addString(name).addNumber(getActivateRate()));

                if (target.isNpc() && Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate())) {
                    NpcInstance npc = (NpcInstance) target;
                    npc.getAggroList().remove(activeChar, true);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }
                getEffects(activeChar, target, true, false);
            }
    }
}