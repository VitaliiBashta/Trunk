package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.CharTemplateHolder;
import l2trunk.gameserver.templates.item.CreateItem;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum CharTemplateParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/char_templates.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("loaded " + CharTemplateHolder.size() + " doors ");
    }

    private void readData(Element rootElement) {
        for (Iterator interator = rootElement.elementIterator(); interator.hasNext(); ) {
            List<CreateItem> items = new ArrayList<>();

            Element element = (org.dom4j.Element) interator.next();
            StatsSet set = new StatsSet();

            int classId = toInt(element.attributeValue("id"));
            String name = element.attributeValue("name");
            set.set("name", name);

            for (Iterator template = element.elementIterator(); template.hasNext(); ) {
                Element templat = (org.dom4j.Element) template.next();
                if (templat.getName().equalsIgnoreCase("set"))
                    set.set(templat.attributeValue("name"), templat.attributeValue("value"));
                else if (templat.getName().equalsIgnoreCase("item")) {
                    int itemId = toInt(templat.attributeValue("id"));
                    int count = toInt(templat.attributeValue("count"));
                    boolean equipable = false;
                    int shortcat = -1;
                    if (templat.attributeValue("equipable") != null)
                        equipable = Boolean.parseBoolean(templat.attributeValue("equipable"));
                    if (templat.attributeValue("shortcut") != null)
                        shortcat = toInt(templat.attributeValue("shortcut"));
                    items.add(new CreateItem(itemId, count, equipable, shortcat));
                }
            }
            CharTemplateHolder.addTemplate(classId, set, items);
        }
    }
}
