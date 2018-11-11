package l2trunk.gameserver.network.serverpackets;


import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.ArrayList;
import java.util.List;

public class HennaEquipList extends L2GameServerPacket {
    private final int _emptySlots;
    private final long _adena;
    private final List<Henna> _hennas = new ArrayList<>();

    public HennaEquipList(Player player) {
        _adena = player.getAdena();
        _emptySlots = player.getHennaEmptySlots();

        List<Henna> list = HennaHolder.getInstance().generateList(player);
        for (Henna element : list)
            if (player.getInventory().getItemByItemId(element.getDyeId()) != null)
                _hennas.add(element);
    }

    @Override
    protected final void writeImpl() {
        writeC(0xee);

        writeQ(_adena);
        writeD(_emptySlots);
        if (_hennas.size() != 0) {
            writeD(_hennas.size());
            for (Henna henna : _hennas) {
                writeD(henna.getSymbolId()); //symbolid
                writeD(henna.getDyeId()); //itemid of dye
                writeQ(henna.getDrawCount());
                writeQ(henna.getPrice());
                writeD(1); //meet the requirement or not
            }
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