package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public class SetupGauge extends L2GameServerPacket {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;

    private final int _charId;
    private final int _dat1;
    private final int _time;

    public SetupGauge(Creature character, int dat1, int time) {
        _charId = character.getObjectId();
        _dat1 = dat1;// color  0-blue   1-red  2-cyan  3-
        _time = time;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x6b);
        writeD(_charId);
        writeD(_dat1);
        writeD(_time);

        writeD(_time); //c2
    }
}