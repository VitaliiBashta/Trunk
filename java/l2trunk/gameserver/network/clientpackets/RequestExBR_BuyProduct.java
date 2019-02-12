package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.ProductHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.ProductItem;
import l2trunk.gameserver.model.ProductItemComponent;
import l2trunk.gameserver.network.serverpackets.ExBR_BuyProduct;
import l2trunk.gameserver.network.serverpackets.ExBR_GamePoint;
import l2trunk.gameserver.templates.item.ItemTemplate;

public final class RequestExBR_BuyProduct extends L2GameClientPacket {
    private int _productId;
    private int count;

    @Override
    protected void readImpl() {
        _productId = readD();
        count = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        if (count > 99 || count < 0)
            return;

        ProductItem product = ProductHolder.getInstance().getProduct(_productId);
        if (product == null) {
            activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
            return;
        }

        if ((System.currentTimeMillis() < product.getStartTimeSale()) || (System.currentTimeMillis() > product.getEndTimeSale())) {
            activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SALE_PERIOD_ENDED));
            return;
        }

        int totalPoints = product.getPoints() * count;

        if (totalPoints < 0) {
            activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
            return;
        }

        final long gamePointSize = activeChar.getPremiumPoints();

        if (totalPoints > gamePointSize) {
            activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
            return;
        }

        int totalWeight = product.getComponents().stream()
                .mapToInt(com -> com.weight).sum();

        totalWeight *= count; //увеличиваем вес согласно количеству

        int totalCount = 0;

        for (ProductItemComponent com : product.getComponents()) {
            ItemTemplate item = ItemHolder.getTemplate(com.itemId);
            if (item == null) {
                activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
                return; //what
            }
            totalCount += item.stackable ? 1 : com.count * count;
        }

        if (!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight)) {
            activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
            return;
        }

        activeChar.reducePremiumPoints(totalPoints);

        for (ProductItemComponent $comp : product.getComponents()) {
            activeChar.getInventory().addItem($comp.itemId, $comp.count * count, "RequestExBR_BuyProduct");
        }

        activeChar.sendPacket(new ExBR_GamePoint(activeChar));
        activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
        activeChar.sendChanges();
    }
}