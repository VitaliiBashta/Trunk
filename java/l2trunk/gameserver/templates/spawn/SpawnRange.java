package l2trunk.gameserver.templates.spawn;

import l2trunk.gameserver.utils.Location;

public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}
