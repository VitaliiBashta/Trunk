package l2trunk.gameserver.listener.game;

import l2trunk.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	void onShutdown();
}
