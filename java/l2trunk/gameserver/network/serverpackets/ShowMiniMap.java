package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;

public class ShowMiniMap extends L2GameServerPacket {
    private final int _mapId;
    private final int _period;

    public ShowMiniMap(Player player, int mapId) {
        _mapId = mapId;
        _period = SevenSigns.INSTANCE.getCurrentPeriod();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xa3);
        writeD(_mapId);
        writeC(_period);
    }
}