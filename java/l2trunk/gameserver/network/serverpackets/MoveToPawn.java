package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public class MoveToPawn extends L2GameServerPacket {
    private final int _chaId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _tx;
    private final int _ty;
    private final int _tz;

    public MoveToPawn(Creature cha, Creature target, int distance) {
        _chaId = cha.getObjectId();
        _targetId = target.getObjectId();
        _distance = distance;
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _tx = target.getX();
        _ty = target.getY();
        _tz = target.getZ();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x72);

        writeD(_chaId);
        writeD(_targetId);
        writeD(_distance);

        writeD(_x);
        writeD(_y);
        writeD(_z);

        writeD(_tx);
        writeD(_ty);
        writeD(_tz);
    }
}