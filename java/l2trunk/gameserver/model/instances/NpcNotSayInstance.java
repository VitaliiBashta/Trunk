package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance {
    public NpcNotSayInstance(final int objectID, final NpcTemplate template) {
        super(objectID, template);
        setHasChatWindow(false);
    }
}
