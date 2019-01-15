package l2trunk.gameserver.instancemanager;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.templates.mapregion.RegionData;
import l2trunk.gameserver.utils.Location;

public final class MapRegionHolder  {
    private static final MapRegionHolder _instance = new MapRegionHolder();
    private static final RegionData[][][] map = new RegionData[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][0];

    private MapRegionHolder() {
    }

    public static MapRegionHolder getInstance() {
        return _instance;
    }

    private static int regionX(int x) {
        return (x - World.MAP_MIN_X >> 15);
    }

    private static int regionY(int y) {
        return (y - World.MAP_MIN_Y >> 15);
    }

    public static void addRegionData(RegionData rd) {
        for (int x = regionX(rd.getTerritory().getXmin()); x <= regionX(rd.getTerritory().getXmax()); x++)
            for (int y = regionY(rd.getTerritory().getYmin()); y <= regionY(rd.getTerritory().getYmax()); y++) {
                map[x][y] = ArrayUtils.add(map[x][y], rd);
            }
    }

    public <T extends RegionData> T getRegionData(Class<T> clazz, GameObject o) {
        return getRegionData(clazz, o.getLoc());
    }

    public <T extends RegionData> T getRegionData(Class<T> clazz, Location loc) {
        return getRegionData(clazz, loc.getX(), loc.getY(), loc.getZ());
    }

    private <T extends RegionData> T getRegionData(Class<T> clazz, int x, int y, int z) {
        for (RegionData rd : map[regionX(x)][regionY(y)]) {
            if (rd.getClass() != clazz)
                continue;
            if (rd.getTerritory().isInside(new Location(x, y, z)))
                return (T) rd;
        }
        return null;
    }

    public static int size() {
        return World.WORLD_SIZE_X * World.WORLD_SIZE_Y;
    }

    public void clear() {

    }
}
