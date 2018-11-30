package l2trunk.gameserver.model.quest;

import java.util.ArrayList;
import java.util.List;

public final class QuestNpcLogInfo {
    private final List<Integer> _npcIds;
    private final String _varName;
    private final int _maxCount;

    QuestNpcLogInfo(int[] npcIds, String varName, int maxCount) {
        _npcIds = new ArrayList<>();
        for (int id : npcIds) {
            _npcIds.add(id);
        }
        _varName = varName;
        _maxCount = maxCount;
    }

    public List<Integer> getNpcIds() {
        return _npcIds;
    }

    public String getVarName() {
        return _varName;
    }

    public int getMaxCount() {
        return _maxCount;
    }
}
