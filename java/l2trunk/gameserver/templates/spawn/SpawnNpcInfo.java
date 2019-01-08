package l2trunk.gameserver.templates.spawn;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class SpawnNpcInfo {
    private final NpcTemplate template;
    private final int max;
    private final StatsSet parameters;

    public SpawnNpcInfo(int npcId, int max, StatsSet set) {
        template = NpcHolder.getTemplate(npcId);
        if (npcId == 16043)
            System.out.println("PEt!!!");
        this.max = max;
        parameters = set;
    }

    public NpcTemplate getTemplate() {
        return template;
    }

    public int getMax() {
        return max;
    }

    public StatsSet getParameters() {
        return parameters;
    }
}
