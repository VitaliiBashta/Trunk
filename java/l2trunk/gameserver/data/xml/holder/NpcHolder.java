package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.*;
import java.util.stream.Collectors;

public final class NpcHolder extends AbstractHolder {
    private static final NpcHolder _instance = new NpcHolder();

    private final Map<Integer, NpcTemplate> _npcs = new HashMap<>(20000);

    private NpcHolder() {
    }

    public static NpcHolder getInstance() {
        return _instance;
    }

    public void addTemplate(NpcTemplate template) {
        _npcs.put(template.npcId, template);
    }

    public NpcTemplate getTemplate(int id) {
        if (!_npcs.containsKey(id)) {
            warn("Not defined npc id : " + id + ", or out of range!", new Exception());
        }
        return _npcs.get(id);
    }

    public NpcTemplate getTemplateByName(String NpcName) {
        return _npcs.values().stream().filter(a -> a.name.equals(NpcName)).findFirst().orElse(null);
    }

    public List<NpcTemplate> getAllOfLevel(int lvl) {
        return _npcs.values().stream().filter(entry -> entry.level == lvl).collect(Collectors.toList());
    }

    public Collection<NpcTemplate> getAll() {
        return _npcs.values();
    }

    @Override
    protected void process() {
    }

    @Override
    public int size() {
        return _npcs.size();
    }

    @Override
    public void clear() {
        _npcs.clear();
    }
}
