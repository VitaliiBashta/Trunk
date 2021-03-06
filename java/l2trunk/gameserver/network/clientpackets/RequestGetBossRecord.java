package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExGetBossRecord;
import l2trunk.gameserver.network.serverpackets.ExGetBossRecord.BossRecordInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Format: (ch) d
 */
public final class RequestGetBossRecord extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        int totalPoints = 0;
        int ranking = 0;

        if (activeChar == null)
            return;

        List<BossRecordInfo> list = new ArrayList<>();
        Map<Integer, Integer> points = RaidBossSpawnManager.INSTANCE.getPointsForOwnerId(activeChar.objectId());
        if (points != null && !points.isEmpty())
            for (Map.Entry<Integer, Integer> e : points.entrySet())
                switch (e.getKey()) {
                    case -1: // RaidBossSpawnManager.KEY_RANK
                        ranking = e.getValue();
                        break;
                    case 0: //  RaidBossSpawnManager.KEY_TOTAL_POINTS
                        totalPoints = e.getValue();
                        break;
                    default:
                        list.add(new BossRecordInfo(e.getKey(), e.getValue(), 0));
                }

        activeChar.sendPacket(new ExGetBossRecord(ranking, totalPoints, list));
    }
}