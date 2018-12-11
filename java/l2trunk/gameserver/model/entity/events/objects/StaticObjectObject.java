package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.data.xml.holder.StaticObjectHolder;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.StaticObjectInstance;

public final class StaticObjectObject implements SpawnableObject {
    private final int uid;
    private StaticObjectInstance instance;

    public StaticObjectObject(int id) {
        uid = id;
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        instance = StaticObjectHolder.getObject(uid);
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        //
    }

    @Override
    public void refreshObject(GlobalEvent event) {
        if (!event.isInProgress())
            instance.removeEvent(event);
        else
            instance.addEvent(event);
    }

    public void setMeshIndex(int id) {
        instance.setMeshIndex(id);
        instance.broadcastInfo(false);
    }

    public int getUId() {
        return uid;
    }
}
