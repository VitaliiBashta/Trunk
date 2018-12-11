package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.StaticObjectHolder;
import l2trunk.gameserver.templates.StaticObjectTemplate;
import l2trunk.gameserver.templates.StatsSet;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

public enum StaticObjectParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final Path xmlFile = Config.DATAPACK_ROOT.resolve("data/staticobjects.xml");

    public void load() {
        ParserUtil.INSTANCE.load(xmlFile).forEach(this::readData);
        LOG.info("loaded " + StaticObjectHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element staticObjectElement = iterator.next();

            StatsSet set = new StatsSet();
            set.set("uid", staticObjectElement.attributeValue("id"));
            set.set("stype", staticObjectElement.attributeValue("stype"));
            set.set("path", staticObjectElement.attributeValue("path"));
            set.set("map_x", staticObjectElement.attributeValue("map_x"));
            set.set("map_y", staticObjectElement.attributeValue("map_y"));
            set.set("name", staticObjectElement.attributeValue("name"));
            set.set("x", staticObjectElement.attributeValue("x"));
            set.set("y", staticObjectElement.attributeValue("y"));
            set.set("z", staticObjectElement.attributeValue("z"));
            set.set("spawn", staticObjectElement.attributeValue("spawn"));

            StaticObjectHolder.addTemplate(new StaticObjectTemplate(set));
        }
    }
}
