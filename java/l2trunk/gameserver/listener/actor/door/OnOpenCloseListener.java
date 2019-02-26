package l2trunk.gameserver.listener.actor.door;

import l2trunk.gameserver.listener.CharListener;
import l2trunk.gameserver.model.instances.DoorInstance;

public interface OnOpenCloseListener extends CharListener {
    void onOpen(DoorInstance doorInstance);

    void onClose(DoorInstance doorInstance);
}
