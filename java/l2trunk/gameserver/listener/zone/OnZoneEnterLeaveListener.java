package l2trunk.gameserver.listener.zone;

import l2trunk.commons.listener.Listener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener {
    void onZoneEnter(Zone zone, Player actor);

    default void onZoneLeave(Zone zone, Player actor){}
}
