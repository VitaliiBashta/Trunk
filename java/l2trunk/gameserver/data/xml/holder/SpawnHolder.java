package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;

import java.util.*;

public final class SpawnHolder extends AbstractHolder {
    private static final SpawnHolder _instance = new SpawnHolder();

    private final Map<String, List<SpawnTemplate>> _spawns = new HashMap<>();

    public static SpawnHolder getInstance() {
        return _instance;
    }

    public void addSpawn(String group, SpawnTemplate spawn) {
        List<SpawnTemplate> spawns = _spawns.computeIfAbsent(group, k -> new ArrayList<>());
        spawns.add(spawn);
    }

    public List<SpawnTemplate> getSpawn(String name) {
        List<SpawnTemplate> template = _spawns.get(name);
        return template == null ? Collections.<SpawnTemplate>emptyList() : template;
    }

    @Override
    public int size() {
        int i = 0;
        for (List<?> l : _spawns.values())
            i += l.size();

        return i;
    }

    @Override
    public void clear() {
        _spawns.clear();
    }

    public Map<String, List<SpawnTemplate>> getSpawns() {
        return _spawns;
    }
}
