package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.math.SafeMath;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.ManorManagerInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.manor.SeedProduction;

/**
 * Format: cdd[dd]
 * c    // id (0xC5)
 * <p>
 * d    // manor id
 * d    // seeds to buy
 * [
 * d    // seed id
 * d    // count
 * ]
 */
public final class RequestBuySeed extends L2GameClientPacket {
    private int count, manorId;
    private int[] items;
    private long[] itemQ;

    @Override
    protected void readImpl() {
        manorId = readD();
        count = readD();

        if (count * 12 > buf.remaining() || count > Short.MAX_VALUE || count < 1) {
            count = 0;
            return;
        }

        items = new int[count];
        itemQ = new long[count];

        for (int i = 0; i < count; i++) {
            items[i] = readD();
            itemQ[i] = readQ();
            if (itemQ[i] < 1) {
                count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || count == 0)
            return;

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
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

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        GameObject target = activeChar.getTarget();

        ManorManagerInstance manor = target instanceof ManorManagerInstance ? (ManorManagerInstance) target : null;
        if (!activeChar.isGM() && (!activeChar.isInRange(manor, Creature.INTERACTION_DISTANCE))) {
            activeChar.sendActionFailed();
            return;
        }

        Castle castle = ResidenceHolder.getCastle(manorId);
        if (castle == null)
            return;

        long totalPrice = 0;
        int slots = 0;
        long weight = 0;

        try {
            for (int i = 0; i < count; i++) {
                int seedId = items[i];
                long count = itemQ[i];
                long price;
                long residual;

                SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
                price = seed.getPrice();
                residual = seed.getCanProduce();

                if (price < 1)
                    return;

                if (residual < count)
                    return;

                totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));

                ItemTemplate item = ItemHolder.getTemplate(seedId);
                if (item == null)
                    return;

                weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, item.weight()));
                if (!item.stackable() || activeChar.getInventory().getItemByItemId(seedId) == null)
                    slots++;
            }

        } catch (ArithmeticException ae) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        activeChar.getInventory().writeLock();
        try {
            if (!activeChar.getInventory().validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }

            if (!activeChar.getInventory().validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            if (!activeChar.reduceAdena(totalPrice, true, "RequestBuySeed")) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            // Adding to treasury for Manor Castle
            castle.addToTreasuryNoTax(totalPrice, false, true);

            // Proceed the purchase
            for (int i = 0; i < count; i++) {
                int seedId = items[i];
                long count = itemQ[i];

                // Update Castle Seeds Amount
                SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
                seed.setCanProduce(seed.getCanProduce() - count);
                castle.updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

                // Add item to Inventory and adjust update packet
                activeChar.getInventory().addItem(seedId, count, "RequestBuySeed");
                activeChar.sendPacket(SystemMessage2.obtainItems(seedId, count, 0));
            }
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.sendChanges();
    }
}