package l2trunk.gameserver.network.serverpackets;

public final class TutorialEnableClientEvent extends L2GameServerPacket {
    private int _event;

    public TutorialEnableClientEvent(int event) {
        _event = event;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xa8);
        writeD(_event);
    }
}