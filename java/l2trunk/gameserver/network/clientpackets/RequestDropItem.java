package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

public final class RequestDropItem extends L2GameClientPacket {
    private int objectId;
    private long count;
    private Location loc;

    @Override
    protected void readImpl() {
        objectId = readD();
        count = readQ();
        loc = Location.of(readD(), readD(), readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (count < 1 || loc.isNull()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isActionsDisabled() || activeChar.isBlocked()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!Config.ALLOW_DISCARDITEM || (!Config.ALLOW_DISCARDITEM_AT_PEACE && activeChar.isInPeaceZone() && !activeChar.isGM())) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestDropItem.Disallowed"));
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isSitting() || activeChar.isDropDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!activeChar.isInRangeSq(loc, 22500) || Math.abs(loc.z - activeChar.getZ()) > 50) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }

        ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (!item.canBeDropped(activeChar, false)) {
            activeChar.sendPacket(SystemMsg.THAT_ITEM_CANNOT_BE_DISCARDED);
            return;
        }

        if (activeChar.isInZone(Zone.ZoneType.SIEGE) || item.getAttachment() != null && !activeChar.isGM()) {
            activeChar.sendMessage("Cannot drop items in Siege Zone!");
            return;
        }

        item.getTemplate().getHandler().dropItem(activeChar, item, count, loc);
    }
}