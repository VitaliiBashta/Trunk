package l2trunk.gameserver.listener.actor.door.impl;

import l2trunk.gameserver.listener.actor.door.OnOpenCloseListener;
import l2trunk.gameserver.model.instances.DoorInstance;

public final class MasterOnOpenCloseListenerImpl implements OnOpenCloseListener {
    private final DoorInstance door;

    public MasterOnOpenCloseListenerImpl(DoorInstance door) {
        this.door = door;
    }

    @Override
    public void onOpen(DoorInstance doorInstance) {
        door.openMe();
    }

    @Override
    public void onClose(DoorInstance doorInstance) {
        door.closeMe();
    }
}
