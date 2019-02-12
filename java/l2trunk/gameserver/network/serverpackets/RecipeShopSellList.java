package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ManufactureItem;

import java.util.List;


public class RecipeShopSellList extends L2GameServerPacket {
    private final int objId;
    private final int curMp;
    private final int maxMp;
    private final long adena;
    private final List<ManufactureItem> createList;

    public RecipeShopSellList(Player buyer, Player manufacturer) {
        objId = manufacturer.objectId();
        curMp = (int) manufacturer.getCurrentMp();
        maxMp = manufacturer.getMaxMp();
        adena = buyer.getAdena();
        createList = manufacturer.getCreateList();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdf);
        writeD(objId);
        writeD(curMp);//Creator's MP
        writeD(maxMp);//Creator's MP
        writeQ(adena);
        writeD(createList.size());
        for (ManufactureItem mi : createList) {
            writeD(mi.getRecipeId());
            writeD(0x00); //unknown
            writeQ(mi.getCost());
        }
    }
}