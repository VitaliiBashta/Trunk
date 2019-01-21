package l2trunk.gameserver.utils;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;

public final class MapUtils {
    private MapUtils() {
    }

    public static int regionX(GameObject o) {
        return (o.getLoc().x - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
    }

    public static int regionY(GameObject o) {
        return (o.getLoc().y - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;
    }

}
