package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;
import java.util.Objects;

public final class DefuseTrap extends Skill {
    public DefuseTrap(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target == null || !target.isTrap()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.stream()
                .filter(Objects::nonNull)
                .filter(GameObject::isTrap)
                .map(trap -> (TrapInstance) trap)
                .filter(trap -> trap.getLevel() <= getPower())
                .forEach(GameObject::deleteMe);
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}