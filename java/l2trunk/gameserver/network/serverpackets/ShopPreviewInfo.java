package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.Inventory;

import java.util.Map;


public final class ShopPreviewInfo extends L2GameServerPacket {
    private final Map<Integer, Integer> itemlist;

    public ShopPreviewInfo(Map<Integer, Integer> itemlist) {
        this.itemlist = itemlist;
    }

    @Override
    protected void writeImpl() {
        writeC(0xF6);
        writeD(Inventory.PAPERDOLL_MAX);

        // Slots
        Inventory.PAPERDOLL_ORDER.forEach(id ->writeD(getFromList(id)));
    }

    private int getFromList(int key) {
        return ((itemlist.get(key) != null) ? itemlist.get(key) : 0);
    }
}