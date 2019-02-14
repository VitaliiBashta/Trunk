package l2trunk.gameserver.network.serverpackets;


import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.ArrayList;
import java.util.List;

public final class HennaUnequipList extends L2GameServerPacket {
    private final int emptySlots;
    private final long adena;
    private final List<Henna> availHenna = new ArrayList<>(3);

    public HennaUnequipList(Player player) {
        adena = player.getAdena();
        emptySlots = player.getHennaEmptySlots();
        for (int i = 1; i <= 3; i++)
            if (player.getHenna(i) != null)
                availHenna.add(player.getHenna(i));
    }

    @Override
    protected final void writeImpl() {
        writeC(0xE6);

        writeQ(adena);
        writeD(emptySlots);
        writeD(availHenna.size());
        availHenna.forEach(henna -> {
            writeD(henna.symbolId); //symbolid
            writeD(henna.dyeId); //itemid of dye
            writeQ(henna.drawCount);
            writeQ(henna.price);
            writeD(1); //meet the requirement or not
        });
    }
}