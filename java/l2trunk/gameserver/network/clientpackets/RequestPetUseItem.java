package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;

public final class RequestPetUseItem extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        activeChar.setActive();

        PetInstance pet = (PetInstance) activeChar.getPet();
        if (pet == null)
            return;

        ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);

        if (item == null || item.getCount() < 1)
            return;

        if (activeChar.isAlikeDead() || pet.isDead() || pet.isOutOfControl()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return;
        }

        // manual pet feeding
        if (pet.tryFeedItem(item))
            return;

        if (Config.ALT_ALLOWED_PET_POTIONS.contains(item.getItemId())) {
            List<Skill> skills = item.getTemplate().getAttachedSkills();
            for (Skill skill : skills) {
                Creature aimingTarget = skill.getAimingTarget(pet, pet.getTarget());
                if (skill.checkCondition(pet.owner, aimingTarget, false, false, true))
                    pet.getAI().cast(skill, aimingTarget, false, false);
            }
            return;
        }

        SystemMessage2 sm = ItemFunctions.checkIfCanEquip(pet, item);
        if (sm == null) {
            if (item.isEquipped())
                pet.inventory.unEquipItem(item);
            else
                pet.inventory.equipItem(item);
            pet.broadcastCharInfo();
            return;
        }

        activeChar.sendPacket(sm);
    }
}