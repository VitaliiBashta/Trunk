package l2trunk.gameserver.network.serverpackets;

public final class ExAskCoupleAction extends L2GameServerPacket {
    private final int objectId;
    private final int socialId;

    public ExAskCoupleAction(int objectId, int socialId) {
        this.objectId = objectId;
        this.socialId = socialId;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xBB);
        writeD(socialId);
        writeD(objectId);
    }
}
