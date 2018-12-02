package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Set;

public final class SiegeToggleNpcObject implements SpawnableObject {
    private final SiegeToggleNpcInstance toggleNpc;
    private final Location location;

    public SiegeToggleNpcObject(int id, int fakeNpcId, Location loc, int hp, Set<String> set) {
        location = loc;

        toggleNpc = (SiegeToggleNpcInstance) NpcHolder.getTemplate(id).getNewInstance();

        toggleNpc.initFake(fakeNpcId);
        toggleNpc.setMaxHp(hp);
        toggleNpc.setZoneList(set);
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        toggleNpc.decayFake();

        if (event.isInProgress())
            toggleNpc.addEvent(event);
        else
            toggleNpc.removeEvent(event);

        toggleNpc.setCurrentHp(toggleNpc.getMaxHp(), true);
        toggleNpc.spawnMe(location);
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        toggleNpc.removeEvent(event);
        toggleNpc.decayFake();
        toggleNpc.decayMe();
    }

    @Override
    public void refreshObject(GlobalEvent event) {
    }

    public SiegeToggleNpcInstance getToggleNpc() {
        return toggleNpc;
    }

    public boolean isAlive() {
        return toggleNpc.isVisible();
    }
}
