package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.NpcInfo;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class DetectTrap extends Skill {
    public DetectTrap(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : activeChar.getAroundCharacters(_skillRadius, 300)) {
            if (target != null && target.isTrap()) {
                TrapInstance trap = (TrapInstance) target;
                if (trap.getLevel() <= getPower()) {
                    trap.setDetected(true);
                    for (Player player : World.getAroundPlayers(trap)) {
                        player.sendPacket(new NpcInfo(trap, player));
                    }
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}