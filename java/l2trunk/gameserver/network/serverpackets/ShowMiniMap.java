package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.SevenSigns;

public final class ShowMiniMap extends L2GameServerPacket {
    private final int mapId;
    private final int period;

    public ShowMiniMap(int mapId) {
        this.mapId = mapId;
        period = SevenSigns.INSTANCE.getCurrentPeriod();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xa3);
        writeD(mapId);
        writeC(period);
    }
}