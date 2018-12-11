package l2trunk.gameserver.network.serverpackets;

import java.util.List;

/**
 * Format: ch ddd [ddd]
 */
public final class ExGetBossRecord extends L2GameServerPacket {
    private final List<BossRecordInfo> _bossRecordInfo;
    private final int _ranking;
    private final int _totalPoints;

    public ExGetBossRecord(int ranking, int totalScore, List<BossRecordInfo> bossRecordInfo) {
        _ranking = ranking; // char ranking
        _totalPoints = totalScore; // char total points
        _bossRecordInfo = bossRecordInfo;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x34);

        writeD(_ranking); // char ranking
        writeD(_totalPoints); // char total points

        writeD(_bossRecordInfo.size()); // list size
        for (BossRecordInfo w : _bossRecordInfo) {
            writeD(w._bossId);
            writeD(w._points);
            writeD(w._unk1);// don`t know
        }
    }

    public static class BossRecordInfo {
        final int _bossId;
        final int _points;
        final int _unk1;

        public BossRecordInfo(int bossId, int points, int unk1) {
            _bossId = bossId;
            _points = points;
            _unk1 = unk1;
        }
    }
}