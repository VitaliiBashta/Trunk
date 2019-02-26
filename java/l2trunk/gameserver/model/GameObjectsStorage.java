package l2trunk.gameserver.model;

import l2trunk.gameserver.model.instances.NpcInstance;

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

    public synchronized static Stream<NpcInstance> getAllNpcs() {
        return objects.values().stream()
                .filter(o -> o instanceof NpcInstance)
                .map(o -> (NpcInstance) o);
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
//        throw new IllegalArgumentException("no getPlayer with id:" + objId);
    }

    public static Stream<Player> getAllPlayersStream() {
        return objects.values().stream()
                .filter(o -> o instanceof Player)
                .map(o -> (Player) o);
    }

    public static long getAllPlayersCount() {
        return getAllPlayersStream().count();
    }

    public static GameObject findObject(int objId) {
        return objects.get(objId);
    }

    public static NpcInstance getByNpcId(int npc_id) {
        return getAllNpcs()
                .filter(npc -> npc.getNpcId() == npc_id)
                .filter(npc -> !npc.isDead())
                .findFirst().orElse(null);
    }

    public static Stream<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive) {
        return getAllByNpcId(npc_id, justAlive, false);
    }

    public static Stream<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive, boolean visible) {
        return getAllNpcs()
                .filter(npc -> (npc.getTemplate() != null))
                .filter(npc -> npc_id == npc.getTemplate().getNpcId())
                .filter(npc -> (!justAlive || !npc.isDead()))
                .filter(npc -> (!visible || npc.isVisible()));
    }

    public static Stream<NpcInstance> getAllByNpcId(List<Integer> npc_ids, boolean justAlive) {
        return getAllNpcs()
                .filter(npc -> npc_ids.contains(npc.getNpcId()))
                .filter(temp -> !justAlive || !temp.isDead());
    }

    public static NpcInstance getNpc(String s) {
        return getAllNpcs()
                .filter(npc -> npc.getName().equalsIgnoreCase(s))
                .filter(npc -> !npc.isDead())
                .findFirst().orElse(null);
    }

    public static NpcInstance getNpc(int objId) {
        return getAllNpcs().filter(n -> n.objectId() == objId).findFirst().orElse(null);
    }

    public static void put(Creature o) {
        objects.put(o.objectId(), o);
    }

    public static GameObject remove(int storedId) {
        return objects.remove(storedId);
    }

}