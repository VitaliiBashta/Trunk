package l2trunk.gameserver.utils;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class NpcUtils {
    private NpcUtils() {
    }

    public static NpcInstance spawnSingle(int npcId, Location loc) {
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, 0);
    }

    public static NpcInstance spawnSingle(int npcId, Location loc, long despawnTime) {
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, despawnTime);
    }

    public static NpcInstance spawnSingle(int npcId, Location loc, Reflection reflection) {
        return spawnSingle(npcId, loc, reflection, 0);
    }

    private static NpcInstance spawnSingle(int npcId, Location loc, Reflection reflection, long despawnTime) {
        NpcInstance npc = NpcHolder.getTemplate(npcId).getNewInstance();
        npc.setSpawnedLoc(loc)
                .setFullHpMp()
                .setHeading(loc.h < 0 ? Rnd.get(0xFFFF) : loc.h)
                .setReflection(reflection)
                .spawnMe(npc.getSpawnedLoc());

        if (despawnTime > 0)
            ThreadPoolManager.INSTANCE.schedule(new GameObjectTasks.DeleteTask(npc), despawnTime);
        return npc;
    }
}
