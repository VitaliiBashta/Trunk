package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.CubicTemplate;

import java.util.HashMap;
import java.util.Map;

public final class CubicHolder {
    private CubicHolder() {
    }

    private static final Map<Integer, CubicTemplate> cubics = new HashMap<>(10);

    public static void addCubicTemplate(CubicTemplate template) {
        cubics.put(template.getId() * 10000 + template.getLevel(), template);
    }

    public static CubicTemplate getTemplate(int id, int level) {
        return cubics.get(id * 10000 + level);
    }

    public static int size() {
        return cubics.size();
    }
}
