package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public class SpawnSimpleObject implements SpawnableObject {
    private final int _npcId;
    private final Location _loc;

    private NpcInstance npc;

    public SpawnSimpleObject(int npcId, Location loc) {
        _npcId = npcId;
        _loc = loc;
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        npc = NpcUtils.spawnSingle(_npcId, _loc, event.getReflection());
        npc.addEvent(event);
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        npc.removeEvent(event);
        npc.deleteMe();
    }

    @Override
    public void refreshObject(GlobalEvent event) {

    }
}
