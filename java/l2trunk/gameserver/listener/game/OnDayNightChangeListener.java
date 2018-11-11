package l2trunk.gameserver.listener.game;

import l2trunk.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener
{
	void onDay();

	void onNight();
}
