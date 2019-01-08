package l2trunk.gameserver.model;

import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GameObjectsStorage {
    private static final Map<Integer, Creature> objects = new ConcurrentHashMap<>();

    private GameObjectsStorage() {
    }

    public static List<NpcInstance> getAllNpcs() {
        return objects.values().stream()
                .filter(o -> o instanceof NpcInstance)
                .map(o -> (NpcInstance) o)
                .collect(Collectors.toList());
    }

    public static GameObject get(int storedId) {
        return objects.get(storedId);
    }

    public static NpcInstance getAsNpc(int storedId) {
        GameObject obj = get(storedId);
        if (obj instanceof NpcInstance) return (NpcInstance) obj;
        throw new IllegalArgumentException("no npc with id:" + storedId);
    }

    public static Player getAsPlayer(int storedId) {
        GameObject o = get(storedId);
        if (o instanceof Player) return (Player) o;
        return null;
    }

    public static Player getPlayer(String name) {
        return getAllPlayersStream()
                .filter(p -> p.getName().equals(name))
                .findFirst().orElse(null);
    }

    public static Player getPlayer(int objId) {
        Creature o = objects.get(objId);
        if (o instanceof Player) return (Player) o;
        return null;
//        throw new IllegalArgumentException("no player with id:" + objId);
    }

    public static Stream<Player> getAllPlayersStream() {
        return objects.values().stream()
                .filter(Objects::nonNull)
                .filter(o -> o instanceof Player)
                .map(o -> (Player) o);
    }
    public static List<Player> getAllPlayers() {
        return getAllPlayersStream()
                .collect(Collectors.toList());
    }

    public static long getAllPlayersCount() {
        return getAllPlayersStream().count();
    }

    public static GameObject findObject(int objId) {
        return objects.get(objId);
    }

    /**
     * использовать только для перебора типа for(L2Player player : getAllPlayersForIterate()) ...
     */
    public static Iterable<NpcInstance> getAllNpcsForIterate() {
        return getAllNpcs();
    }

    public static NpcInstance getByNpcId(int npc_id) {
        NpcInstance result = null;

        for (NpcInstance temp : getAllNpcs())
            if (npc_id == temp.getNpcId()) {
                if (!temp.isDead())
                    return temp;
                result = temp;
            }
        return result;
    }

    public static List<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive) {
        return getAllByNpcId(npc_id, justAlive, false);
    }

    public static List<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive, boolean visible) {
        List<NpcInstance> result = new ArrayList<>();
        for (NpcInstance temp : getAllNpcs())
            if (temp.getTemplate() != null && npc_id == temp.getTemplate().getNpcId() && (!justAlive || !temp.isDead()) && (!visible || temp.isVisible()))
                result.add(temp);
        return result;
    }

    public static List<NpcInstance> getAllByNpcId(List<Integer> npc_ids, boolean justAlive) {
        List<NpcInstance> result = new ArrayList<>();
        for (NpcInstance temp : getAllNpcs())
            if (!justAlive || !temp.isDead())
                for (int npc_id : npc_ids)
                    if (npc_id == temp.getNpcId())
                        result.add(temp);
        return result;
    }

    public static NpcInstance getNpc(String s) {
        List<NpcInstance> npcs = getAllNpcs().stream()
                .filter(npc -> npc.getName().equalsIgnoreCase(s))
                .collect(Collectors.toList());
        if (npcs.size() == 0)
            return null;
        for (NpcInstance temp : npcs)
            if (!temp.isDead())
                return temp;
        if (npcs.size() > 0)
            return npcs.remove(npcs.size() - 1);

        return null;
    }

    public static NpcInstance getNpc(int objId) {
        return getAllNpcs().get(objId);
    }

    public static void put(Creature o) {
        objects.put(o.getObjectId(), o);
    }

    /**
     * пересчитывает StoredId, необходимо при изменении ObjectId
     */
    public static int refreshId(Creature o) {
        return o.getObjectId();
    }

    public static GameObject remove(int storedId) {
        return objects.remove(storedId);
    }

}