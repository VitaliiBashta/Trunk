package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ManufactureItem;
import l2trunk.gameserver.network.serverpackets.RecipeShopMsg;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.TradeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class RequestRecipeShopListSet extends L2GameClientPacket {
    private List<Integer> recipes = new ArrayList<>();
    private List<Long> prices = new ArrayList<>();
    private int count;

    @Override
    protected void readImpl() {
        count = readD();
        if (count * 12 > buf.remaining() || count > Short.MAX_VALUE || count < 1) {
            count = 0;
            return;
        }
        for (int i = 0; i < count; i++) {
            recipes.add(readD());
            prices.add(readQ());
            if (prices.get(i) < 0) {
                count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player manufacturer = getClient().getActiveChar();
        if (manufacturer == null || count == 0)
            return;

        if (!TradeHelper.checksIfCanOpenStore(manufacturer, Player.STORE_PRIVATE_MANUFACTURE)) {
            manufacturer.sendActionFailed();
            return;
        }

        if (count > Config.MAX_PVTCRAFT_SLOTS) {
            manufacturer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        List<ManufactureItem> createList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < count; i++) {
            int recipeId = recipes.get(i);
            long price = prices.get(i);
            if (!manufacturer.findRecipe(recipeId))
                continue;

            ManufactureItem mi = new ManufactureItem(recipeId, price);
            createList.add(mi);
        }

        if (!createList.isEmpty()) {
            manufacturer.setCreateList(createList);
            manufacturer.saveTradeList();
            manufacturer.setPrivateStoreType(Player.STORE_PRIVATE_MANUFACTURE);
            manufacturer.broadcastPacket(new RecipeShopMsg(manufacturer));
            manufacturer.sitDown(null);
            manufacturer.broadcastCharInfo();
        }

        manufacturer.sendActionFailed();
    }
}