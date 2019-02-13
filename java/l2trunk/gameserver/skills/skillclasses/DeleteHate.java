package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

public final class DeleteHate extends Skill {
    public DeleteHate(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target != null && !target.isRaid()) {
            if (activateRate > 0) {
                if (activeChar instanceof Player && ((Player) activeChar).isGM())
                    ((Player)activeChar).sendMessage(new CustomMessage("l2trunk.gameserver.skills.Formulas.Chance").addString(name).addNumber(activateRate));

                if (!Rnd.chance(activateRate))
                    return;
            } else if (target instanceof NpcInstance) {
                NpcInstance npc = (NpcInstance) target;
                npc.getAggroList().clear(false);
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }

            getEffects(activeChar, target);
        }
    }
}
