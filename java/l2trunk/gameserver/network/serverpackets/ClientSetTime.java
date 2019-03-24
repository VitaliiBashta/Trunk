package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.GameTimeController;

public final class ClientSetTime extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new ClientSetTime();

    private ClientSetTime() {
    }

    @Override
    protected final void writeImpl() {
        writeC(0xf2);
        writeD(GameTimeController.INSTANCE.getGameTime()); // time in client minutes
        writeD(6); //constant to match the server time( this determines the speed of the client clock)
    }
}