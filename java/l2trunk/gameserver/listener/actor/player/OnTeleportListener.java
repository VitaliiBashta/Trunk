package l2trunk.gameserver.listener.actor.player;

import l2trunk.gameserver.listener.PlayerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.utils.Location;

public interface OnTeleportListener extends PlayerListener {
    void onTeleport(Player player, Location loc, Reflection reflection);
}
