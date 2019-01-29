package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.quest.QuestNpcLogInfo;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ExQuestNpcLogList extends L2GameServerPacket {
    private final int questId;
    private List<int[]> _logList = Collections.emptyList();

    public ExQuestNpcLogList(QuestState state) {
        questId = state.getQuest().questId;
        int cond = state.getCond();
        List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
        if (vars == null)
            return;

        _logList = new ArrayList<>(vars.size());
        for (QuestNpcLogInfo entry : vars) {
            int[] i = new int[2];
            i[0] = entry.getNpcIds().get(0) + 1000000;
            i[1] = state.getInt(entry.getVarName());
            _logList.add(i);
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC5);
        writeD(questId);
        writeC(_logList.size());
        for (int[] values : _logList) {
            writeD(values[0]);
            writeC(0);      // npc index?
            writeD(values[1]);
        }
    }
}
