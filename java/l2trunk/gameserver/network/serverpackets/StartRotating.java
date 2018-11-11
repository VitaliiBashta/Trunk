package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public class StartRotating extends L2GameServerPacket {
    private final int _charId;
    private final int _degree;
    private final int _side;
    private final int _speed;

    public StartRotating(Creature cha, int degree, int side, int speed) {
        _charId = cha.getObjectId();
        _degree = degree;
        _side = side;
        _speed = speed;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x7a);
        writeD(_charId);
        writeD(_degree);
        writeD(_side);
        writeD(_speed);
    }
}