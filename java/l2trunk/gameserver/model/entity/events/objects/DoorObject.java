package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.DoorInstance;

public final class DoorObject implements SpawnableObject, InitableObject {
    private final int id;
    private DoorInstance door;

    private boolean _weak;

    public DoorObject(int id) {
        this.id = id;
    }

    @Override
    public void initObject(GlobalEvent e) {
        door = e.getReflection().getDoor(id);
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        refreshObject(event);
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        Reflection ref = event.getReflection();
        if (ref == ReflectionManager.DEFAULT) {
            refreshObject(event);
        }
    }

    @Override
    public void refreshObject(GlobalEvent event) {
        if (!event.isInProgress()) {
            door.removeEvent(event);
        } else {
            door.addEvent(event);
        }

        if (door.getCurrentHp() <= 0) {
            door.decayMe();
            door.spawnMe();
        }

        door.setCurrentHp(door.getMaxHp() * (isWeak() ? 0.5 : 1.), true);
        close(event);
    }

    public int getUId() {
        return door.getDoorId();
    }

    public void setUpgradeValue(GlobalEvent event, int val) {
        door.setUpgradeHp(val);
        refreshObject(event);
    }

    public void open(GlobalEvent e) {
        door.openMe(!e.isInProgress());
    }

    public void close(GlobalEvent e) {
        door.closeMe(!e.isInProgress());
    }

    public DoorInstance getDoor() {
        return door;
    }

    private boolean isWeak() {
        return _weak;
    }

    public void setWeak(boolean weak) {
        _weak = weak;
    }
}
