package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public class FinishRotating extends L2GameServerPacket {
    private final int _charId;
    private final int _degree;
    private final int _speed;

    public FinishRotating(Creature player, int degree, int speed) {
        _charId = player.objectId();
        _degree = degree;
        _speed = speed;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x61);
        writeD(_charId);
        writeD(_degree);
        writeD(_speed);
        writeD(0x00); //??
    }
}