package l2trunk.gameserver.model.quest;

import java.util.ArrayList;
import java.util.List;

public final class QuestNpcLogInfo {
    private final List<Integer> npcIds;
    private final String varName;
    private final int maxCount;

    QuestNpcLogInfo(List<Integer> npcIds, String varName, int maxCount) {
        this.npcIds = new ArrayList<>(npcIds);
        this.varName = varName;
        this.maxCount = maxCount;
    }

    public List<Integer> getNpcIds() {
        return npcIds;
    }

    public String getVarName() {
        return varName;
    }

    int getMaxCount() {
        return maxCount;
    }
}
