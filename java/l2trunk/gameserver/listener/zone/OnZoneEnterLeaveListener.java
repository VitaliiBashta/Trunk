package l2trunk.gameserver.listener.zone;

import l2trunk.commons.listener.Listener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	void onZoneEnter(Zone zone, Creature actor);

	void onZoneLeave(Zone zone, Creature actor);
}
