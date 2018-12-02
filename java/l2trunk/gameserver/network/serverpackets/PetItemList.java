package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.List;

public final class PetItemList extends L2GameServerPacket {
    private final List<ItemInstance> items;

    public PetItemList(PetInstance cha) {
        items = cha.getInventory().getItems();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb3);
        writeH(items.size());
        items.forEach(this::writeItemInfo);
    }
}