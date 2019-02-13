package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ProductHolder;
import l2trunk.gameserver.model.ProductItem;
import l2trunk.gameserver.model.ProductItemComponent;

public final class ExBR_ProductInfo extends L2GameServerPacket {
    private final ProductItem productId;

    public ExBR_ProductInfo(int id) {
        productId = ProductHolder.getInstance().getProduct(id);
    }

    @Override
    protected void writeImpl() {
        if (productId == null)
            return;

        writeEx(0xD7);

        writeD(productId.getProductId());  //product id
        writeD(productId.getPoints());      // points
        writeD(productId.getComponents().size());       //size

        productId.getComponents().forEach( com-> {
            writeD(com.itemId);   //item id
            writeD(com.count);  //quality
            writeD(com.weight); //weight
            writeD(com.dropable ? 1 : 0); //0 - dont drop/trade
        });
    }
}