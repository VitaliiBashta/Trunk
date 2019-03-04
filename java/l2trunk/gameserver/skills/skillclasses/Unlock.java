package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.ChestInstance;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class Unlock extends Skill {
    private final int unlockPower;

    public Unlock(StatsSet set) {
        super(set);
        unlockPower = set.getInteger("unlockPower") + 100;
    }

    @Override
    public boolean checkCondition(Player activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target == null || target instanceof ChestInstance && target.isDead()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        if (target instanceof ChestInstance)
            return super.checkCondition(activeChar, target, forceUse, dontMove, first);


        if (!(target instanceof DoorInstance) || unlockPower == 0) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        DoorInstance door = (DoorInstance) target;

        if (door.isOpen()) {
            activeChar.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
            return false;
        }

        if (!door.isUnlockable()) {
            activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
            return false;
        }

        if (door.getKey() > 0) // ключ не подходит к двери
        {
            activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
            return false;
        }

        if (unlockPower - door.getLevel() * 100 < 0){ // Дверь слишком высокого уровня
            activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target instanceof DoorInstance) {
            DoorInstance door = (DoorInstance) target;
            if (!door.isOpen() && (door.getKey() > 0 || Rnd.chance(unlockPower - door.getLevel() * 100))) {
                door.openMe(true);
            } else
                activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
        } else if (target instanceof ChestInstance) {
            ChestInstance chest = (ChestInstance) target;
            if (!chest.isDead())
                chest.tryOpen((Player) activeChar, this);
        }
    }
}