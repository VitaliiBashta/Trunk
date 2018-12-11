package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.spawn.SpawnTemplate;

import java.util.*;

public final class SpawnHolder {
    private static final Map<String, List<SpawnTemplate>> SPAWNS = new HashMap<>();

    private SpawnHolder() {
    }

    public static void addSpawn(String group, SpawnTemplate spawn) {
        List<SpawnTemplate> spawns = SPAWNS.computeIfAbsent(group, k -> new ArrayList<>());
        spawns.add(spawn);
    }

    public static List<SpawnTemplate> getSpawn(String name) {
        List<SpawnTemplate> template = SPAWNS.get(name);
        return template == null ? Collections.emptyList() : template;
    }

    public static int size() {
        return SPAWNS.values().stream()
                .map(List::size)
                .mapToInt(i -> i).sum();
    }

    public void clear() {
        SPAWNS.clear();
    }

    public static Map<String, List<SpawnTemplate>> getSpawns() {
        return SPAWNS;
    }
}
