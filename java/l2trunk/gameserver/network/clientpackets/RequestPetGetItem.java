package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;

public final class RequestPetGetItem extends L2GameClientPacket {
    // format: cd
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

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        Summon summon = activeChar.getPet();
        if (summon instanceof PetInstance && !summon.isDead() && !summon.isActionsDisabled()) {
            ItemInstance item = (ItemInstance) activeChar.getVisibleObject(_objectId);
            if (item == null) {
                activeChar.sendActionFailed();
                return;
            }

            if (!ItemFunctions.checkIfCanPickup(summon, item)) {
                SystemMessage2 sm;
                if (item.getItemId() == 57) {
                    sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    sm.addInteger((int) item.getCount());
                } else {
                    sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    sm.addItemName(item.getItemId());
                }
                sendPacket(sm);
                activeChar.sendActionFailed();
                return;
            }

            summon.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
        } else {
            activeChar.sendActionFailed();
        }

    }
}