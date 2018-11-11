package l2trunk.gameserver.network.serverpackets;

public class ExAskCoupleAction extends L2GameServerPacket {
    private final int _objectId;
    private final int _socialId;

    public ExAskCoupleAction(int objectId, int socialId) {
        _objectId = objectId;
        _socialId = socialId;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xBB);
        writeD(_socialId);
        writeD(_objectId);
    }
}
