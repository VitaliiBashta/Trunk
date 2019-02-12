package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestChangePetName extends L2GameClientPacket {
    private String name;

    @Override
    protected void readImpl() {
        name = readS();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        Summon summon = activeChar.getPet();
        PetInstance pet;
        if (summon instanceof PetInstance) {
            pet = (PetInstance) summon;
        } else return;

        if (pet.isDefaultName()) {
            if (name.length() < 1 || name.length() > 25) {
                activeChar.sendPacket(SystemMsg.YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH);
                return;
            }
            pet.setName("." + name);
            pet.broadcastCharInfo();
            pet.updateControlItem();
        }
    }
}