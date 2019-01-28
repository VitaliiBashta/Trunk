package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.skills.TimeStamp;

public final class ExUseSharedGroupItem extends L2GameServerPacket {
    private final int itemId;
    private final int grpId;
    private final int remainedTime;
    private final int totalTime;

    public ExUseSharedGroupItem(int grpId, TimeStamp timeStamp) {
        this.grpId = grpId;
        itemId = timeStamp.id;
        remainedTime = (int) (timeStamp.getReuseCurrent() / 1000);
        totalTime = (int) (timeStamp.getReuseBasic() / 1000);
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x4a);

        writeD(itemId);
        writeD(grpId);
        writeD(remainedTime);
        writeD(totalTime);
    }
}