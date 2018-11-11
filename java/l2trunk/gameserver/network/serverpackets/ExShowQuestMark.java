package l2trunk.gameserver.network.serverpackets;

public class ExShowQuestMark extends L2GameServerPacket {
    private final int _questId;

    public ExShowQuestMark(int questId) {
        _questId = questId;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x21);
        writeD(_questId);
    }
}