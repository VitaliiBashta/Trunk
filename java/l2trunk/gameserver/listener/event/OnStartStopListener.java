package l2trunk.gameserver.listener.event;

import l2trunk.gameserver.listener.EventListener;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener
{
	void onStart(GlobalEvent event);

	void onStop(GlobalEvent event);
}
