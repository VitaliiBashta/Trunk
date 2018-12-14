package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.AggroList.AggroInfo;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class ShiftAggression extends Skill {
    public ShiftAggression(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar.getPlayer() == null)
            return;

        for (Creature target : targets)
            if (target != null) {
                if (!target.isPlayer())
                    continue;

                Player player = (Player) target;

                for (NpcInstance npc : World.getAroundNpc(activeChar, getSkillRadius(), getSkillRadius())) {
                    AggroInfo ai = npc.getAggroList().get(activeChar);
                    if (ai == null)
                        continue;
                    npc.getAggroList().addDamageHate(player, 0, ai.hate);
                    npc.getAggroList().remove(activeChar, true);
                }
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}
