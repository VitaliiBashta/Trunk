package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class DeleteHate extends Skill {
    public DeleteHate(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {

                if (target.isRaid())
                    continue;

                if (getActivateRate() > 0) {
                    if (activeChar.isPlayer() && ((Player) activeChar).isGM())
                        activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.Formulas.Chance", (Player) activeChar).addString(getName()).addNumber(getActivateRate()));

                    if (!Rnd.chance(getActivateRate()))
                        return;
                }

                if (target.isNpc()) {
                    NpcInstance npc = (NpcInstance) target;
                    npc.getAggroList().clear(false);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }

                getEffects(activeChar, target, false, false);
            }
    }
}