package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.templates.CubicTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CubicHolder extends AbstractHolder {
    private static CubicHolder instance =new CubicHolder() ;

    private final Map<Integer, CubicTemplate> cubics = new HashMap<>(10);


    private CubicHolder() {
    }

    public static CubicHolder getInstance() {
            return instance;
    }

    public void addCubicTemplate(CubicTemplate template) {
        cubics.put(hash(template.getId(), template.getLevel()), template);
    }

    public CubicTemplate getTemplate(int id, int level) {
        return cubics.get(hash(id, level));
    }

    private int hash(int id, int level) {
        return id * 10000 + level;
    }

    @Override
    public int size() {
        return cubics.size();
    }

    @Override
    public void clear() {
        cubics.clear();
    }
}
