package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.instances.StaticObjectInstance;
import l2trunk.gameserver.utils.Location;

public final class StaticObjectTemplate {
    public final int uid;
    public final int type; // 0 - signs, 1 - throne, 2 - starter town map, 3 - airship control key
    public final String filePath;
    public final int mapX;
    public final int mapY;
    private final String name;
    private final int x;
    private final int y;
    private final int z;
    public final boolean spawn;

    public StaticObjectTemplate(StatsSet set) {
        uid = set.getInteger("uid");
        type = set.getInteger("stype");
        mapX = set.getInteger("map_x");
        mapY = set.getInteger("map_y");
        filePath = set.getString("path");
        name = set.getString("name");
        x = set.getInteger("x");
        y = set.getInteger("y");
        z = set.getInteger("z");
        spawn = set.getBool("spawn");
    }

    public String getName() {
        return name;
    }

    public StaticObjectInstance newInstance() {
        StaticObjectInstance instance = new StaticObjectInstance(IdFactory.getInstance().getNextId(), this);

        instance.spawnMe(Location.of(x, y, z));

        return instance;
    }
}
