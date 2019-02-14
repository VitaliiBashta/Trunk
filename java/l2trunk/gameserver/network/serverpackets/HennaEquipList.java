package l2trunk.gameserver.network.serverpackets;


import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.List;
import java.util.stream.Collectors;

public final class HennaEquipList extends L2GameServerPacket {
    private final int _emptySlots;
    private final long _adena;
    private final List<Henna> hennas;

    public HennaEquipList(Player player) {
        _adena = player.getAdena();
        _emptySlots = player.getHennaEmptySlots();

        hennas = HennaHolder.generateStream(player)
                .filter(element -> player.haveItem(element.dyeId))
                .collect(Collectors.toList());
    }

    @Override
    protected final void writeImpl() {
        writeC(0xee);

        writeQ(_adena);
        writeD(_emptySlots);
        if (hennas.size() != 0) {
            writeD(hennas.size());
            hennas.forEach(henna -> {
                writeD(henna.symbolId); //symbolid
                writeD(henna.dyeId); //itemid of dye
                writeQ(henna.drawCount);
                writeQ(henna.price);
                writeD(1); //meet the requirement or not
            });
        } else {
            writeD(0x01);
            writeD(0x00);
            writeD(0x00);
            writeQ(0x00);
            writeQ(0x00);
            writeD(0x00);
        }
    }
}