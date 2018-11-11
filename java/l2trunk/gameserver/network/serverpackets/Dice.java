package l2trunk.gameserver.network.serverpackets;

public class Dice extends L2GameServerPacket {
    private final int _playerId;
    private final int _itemId;
    private final int _number;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * 0xd4 Dice         dddddd
     *
     * @param _characters
     */
    public Dice(int playerId, int itemId, int number, int x, int y, int z) {
        _playerId = playerId;
        _itemId = itemId;
        _number = number;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xda);
        writeD(_playerId); // object id of player
        writeD(_itemId); //	item id of dice (spade)  4625,4626,4627,4628
        writeD(_number); // number rolled
        writeD(_x); // x
        writeD(_y); // y
        writeD(_z); // z
    }
}