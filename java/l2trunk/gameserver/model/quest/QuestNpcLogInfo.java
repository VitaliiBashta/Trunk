package l2trunk.gameserver.model.quest;

import java.util.ArrayList;
import java.util.List;

public final class QuestNpcLogInfo {
    public final List<Integer> npcIds;
    public final String varName;
    final int maxCount;

    QuestNpcLogInfo(List<Integer> npcIds, String varName, int maxCount) {
        this.npcIds = new ArrayList<>(npcIds);
        this.varName = varName;
        this.maxCount = maxCount;
    }
}
