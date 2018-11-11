package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

/**
 * 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00    ?*..L........f..
 * 0010: 00 7c f1 ff ff                                     .|...
 * <p>
 * format   dd ddd
 */
public class ChangeWaitType extends L2GameServerPacket {
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;
    private final int _objectId;
    private final int _moveType;
    private final int _x;
    private final int _y;
    private final int _z;

    public ChangeWaitType(Creature cha, int newMoveType) {
        _objectId = cha.getObjectId();
        _moveType = newMoveType;
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x29);
        writeD(_objectId);
        writeD(_moveType);
        writeD(_x);
        writeD(_y);
        writeD(_z);
    }
}