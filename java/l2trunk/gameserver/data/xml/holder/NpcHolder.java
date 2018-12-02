package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NpcHolder {
    private static final Logger LOG = LoggerFactory.getLogger(NpcHolder.class);

    private static final Map<Integer, NpcTemplate> npcs = new HashMap<>(20000);

    private NpcHolder() {
    }

    public static void addTemplate(NpcTemplate template) {
        npcs.put(template.npcId, template);
    }

    public static NpcTemplate getTemplate(int id) {
        if (!npcs.containsKey(id)) {
            LOG.warn("Not defined npc id : " + id + ", or out of range!", new Exception());
        }
        return npcs.get(id);
    }

    public static NpcTemplate getTemplateByName(String NpcName) {
        return npcs.values().stream().filter(a -> a.name.equals(NpcName)).findFirst().orElse(null);
    }

    public static Collection<NpcTemplate> getAll() {
        return npcs.values();
    }

    public static List<NpcTemplate> getAllOfLevel(int lvl) {
        return npcs.values().stream().filter(entry -> entry.level == lvl).collect(Collectors.toList());
    }

    public int size() {
        return npcs.size();
    }

    public void clear() {
        npcs.clear();
    }
}
