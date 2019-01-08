package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.utils.Location;

/**
 * Примеры пакетов:
 * <p>
 * Ставит флажок на карте и показывает стрелку на компасе:
 * EB 00 00 00 00 01 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 * Убирает флажок и стрелку
 * EB 02 00 00 00 02 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 */
public final class RadarControl extends L2GameServerPacket {
    private final Location loc;
    private final int _type;
    private final int _showRadar;

    public RadarControl(int showRadar, int type, Location loc) {
        _showRadar = showRadar; // showRadar?? 0 = showRadar; 1 = delete radar;
        _type = type; // 1 - только стрелка над головой, 2 - флажок на карте
        this.loc = loc;
    }

    public RadarControl(int showRadar, int type) {
        this(showRadar,type,0,0,0);
    }
    public RadarControl(int showRadar, int type, int x, int y, int z) {
        this(showRadar, type, new Location(x, y, z));
    }

    @Override
    protected final void writeImpl() {
        writeC(0xf1);
        writeD(_showRadar);
        writeD(_type); //maybe type
        writeD(loc.x); //x
        writeD(loc.y); //y
        writeD(loc.z); //z
    }
}