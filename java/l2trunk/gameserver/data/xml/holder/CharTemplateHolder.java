package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.templates.PlayerTemplate;
import l2trunk.gameserver.templates.item.CreateItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CharTemplateHolder {
    private final static Map<Integer, PlayerTemplate> TEMPLATES = new HashMap<>();

    private CharTemplateHolder() {
    }

    public static void addTemplate(int classId, StatsSet set, List<CreateItem> items) {
        set.set("collision_radius", set.get("male_collision_radius"));
        set.set("collision_height", set.get("male_collision_height"));
        TEMPLATES.put(classId, new PlayerTemplate(classId, set, true, items));

        set.set("collision_radius", set.get("female_collision_radius"));
        set.set("collision_height", set.get("female_collision_height"));
        TEMPLATES.put(classId | 0x100, new PlayerTemplate(classId, set, false, items));
    }

    public static PlayerTemplate getTemplate(ClassId classId, boolean female) {
        return getTemplate(classId.id, female);
    }

    public static PlayerTemplate getTemplate(int classId, boolean female) {
        int key = classId;
        if (female)
            key |= 0x100;
        return TEMPLATES.get(key);
    }

    public static int size() {
        return TEMPLATES.size();
    }

    public static void clear() {
        TEMPLATES.clear();
    }
}
