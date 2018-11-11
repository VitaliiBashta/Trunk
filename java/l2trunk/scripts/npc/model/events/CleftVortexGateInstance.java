package l2trunk.scripts.npc.model.events;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class CleftVortexGateInstance extends NpcInstance {
    public CleftVortexGateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setShowName(false);
    }
}
