package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.instances.StaticObjectInstance;
import l2trunk.gameserver.templates.StaticObjectTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class StaticObjectHolder {
    private static final Logger LOG = LoggerFactory.getLogger(StaticObjectHolder.class);
    private static final Map<Integer, StaticObjectTemplate> TEMPLATES = new HashMap<>();
    private static final Map<Integer, StaticObjectInstance> SPAWNED = new HashMap<>();

    private StaticObjectHolder() {
    }

    public static void addTemplate(StaticObjectTemplate template) {
        TEMPLATES.put(template.uid, template);
    }

    public static void spawnAll() {
        for (StaticObjectTemplate template : TEMPLATES.values())
            if (template.spawn) {
                StaticObjectInstance obj = template.newInstance();

                SPAWNED.put(template.uid, obj);
            }
        LOG.info("spawned: " + SPAWNED.size() + " static object(s).");
    }

    public static int size() {
        return TEMPLATES.size();
    }

    public static void clear() {
        TEMPLATES.clear();
    }

    public StaticObjectTemplate getTemplate(int id) {
        return TEMPLATES.get(id);
    }

    public static StaticObjectInstance getObject(int id) {
        return SPAWNED.get(id);
    }
}
