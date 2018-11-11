package l2trunk.gameserver.listener.actor.player;

import l2trunk.gameserver.listener.PlayerListener;
import l2trunk.gameserver.model.Player;

public interface OnPlayerEnterListener extends PlayerListener
{
	void onPlayerEnter(Player player);
}
