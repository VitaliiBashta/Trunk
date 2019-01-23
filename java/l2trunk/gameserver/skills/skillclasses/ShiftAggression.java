package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;

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

                World.getAroundNpc(activeChar, skillRadius, skillRadius)
                        .filter(npc -> npc.getAggroList().get(activeChar) != null)
                        .forEach(npc -> {
                            npc.getAggroList().addDamageHate(player, 0, npc.getAggroList().get(activeChar).hate);
                            npc.getAggroList().remove(activeChar, true);
                        });
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}
