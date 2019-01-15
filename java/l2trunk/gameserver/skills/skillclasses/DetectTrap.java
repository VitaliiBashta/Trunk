package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.NpcInfo;

import java.util.List;

public final class DetectTrap extends Skill {
    public DetectTrap(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        activeChar.getAroundCharacters(skillRadius, 300)
                .filter(GameObject::isTrap)
                .map(target -> (TrapInstance) target)
                .filter(trap -> trap.getLevel() <= getPower())
                .forEach(trap -> {
                    trap.setDetected(true);
                    World.getAroundPlayers(trap)
                            .forEach(p -> p.sendPacket(new NpcInfo(trap, p)));

                });
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}