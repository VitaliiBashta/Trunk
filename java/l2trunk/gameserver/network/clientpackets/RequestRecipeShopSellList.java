package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.RecipeShopSellList;

/**
 * Возврат к списку из информации о рецепте
 */
public class RequestRecipeShopSellList extends L2GameClientPacket {
    private int _manufacturerId;

    @Override
    protected void readImpl() {
        _manufacturerId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isActionsDisabled() || activeChar.isBlocked()) {
            activeChar.sendActionFailed();
            return;
        }

        Player manufacturer = (Player) activeChar.getVisibleObject(_manufacturerId);
        if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(activeChar, Creature.INTERACTION_DISTANCE)) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new RecipeShopSellList(activeChar, manufacturer));
    }
}