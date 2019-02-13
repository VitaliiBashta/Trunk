package l2trunk.gameserver.network.serverpackets;

/**
 * <p>
 * sample  b9 73 5d 30 49 01 00 00 00 00 00
 * <p>
 * format dhd	(objectid, color, unk)
 * <p>
 * color 	-xx -> -9 	red<p>
 * -8  -> -6	light-red<p>
 * -5	-> -3	yellow<p>
 * -2	-> 2    white<p>
 * 3	-> 5	green<p>
 * 6	-> 8	light-blue<p>
 * 9	-> xx	blue<p>
 * <p>
 * usually the color equals the occupation difference to the selected target
 */
public final class MyTargetSelected extends L2GameServerPacket {
    private final int _objectId;
    private final int _color;

    public MyTargetSelected(int objectId, int color) {
        _objectId = objectId;
        _color = color;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb9);
        writeD(_objectId);
        writeH(_color);
        writeD(0x00);
    }
}