package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.PetDataTable;

import java.util.List;

public final class PetSummon extends Skill {
    public PetSummon(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player == null)
            return false;

        if (player.getPetControlItem() == null)
            return false;

        int npcId = PetDataTable.getSummonId(player.getPetControlItem());
        if (npcId == 0)
            return false;

        if (player.isInCombat()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
            return false;
        }

        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        if (player.isMounted() || player.getPet() != null) {
            player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
            return false;
        }

        if (player.isInBoat()) {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
            return false;
        }

        if (player.isInFlyingTransform())
            return false;

        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
            return false;
        }

        if (player.isCursedWeaponEquipped()) {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
            return false;
        }

        if (player.isJailed()) {
            player.sendMessage("You cannot use that item in Jail!");
            return false;
        }
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> targets) {
        Player activeChar = (Player)creature;

        activeChar.summonPet();

        if (isSSPossible())
            creature.unChargeShots(isMagic());
    }
}