package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.quest.QuestNpcLogInfo;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.ArrayList;
import java.util.List;


public final class ExQuestNpcLogList extends L2GameServerPacket {
    private final int questId;
    private List<int[]> logList;

    public ExQuestNpcLogList(QuestState state) {
        logList = new ArrayList<>();
        questId = state.quest.id;
        List<QuestNpcLogInfo> vars = state.quest.getNpcLogList(state.getCond());
        if (vars == null)
            return;

        logList = new ArrayList<>(vars.size());
        for (QuestNpcLogInfo entry : vars) {
            int[] i = new int[2];
            i[0] = entry.npcIds.get(0) + 1000000;
            i[1] = state.getInt(entry.varName);
            logList.add(i);
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC5);
        writeD(questId);
        writeC(logList.size());
        for (int[] values : logList) {
            writeD(values[0]);
            writeC(0);      // npc index?
            writeD(values[1]);
        }
    }
}
