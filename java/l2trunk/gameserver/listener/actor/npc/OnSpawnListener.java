package l2trunk.gameserver.listener.actor.npc;

import l2trunk.gameserver.listener.NpcListener;
import l2trunk.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	void onSpawn(NpcInstance actor);
}
