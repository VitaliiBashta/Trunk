package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressArmorHolder;
import l2trunk.gameserver.model.DressArmorData;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum DressArmorParser {
    INSTANCE;
    private final Path xml = Config.DATAPACK_ROOT.resolve("data/dress/armor.xml");
    private Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + DressArmorHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("dress"); iterator.hasNext(); ) {
            Element dress = iterator.next();

            Element set = dress.element("set");

            int chest = toInt(set.attributeValue("chest"));
            int legs = toInt(set.attributeValue("legs"));
            int gloves = toInt(set.attributeValue("gloves"));
            int feet = toInt(set.attributeValue("feet"));

            Element price = dress.element("price");
            int itemId = toInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            DressArmorHolder.addDress(new DressArmorData(chest, legs, gloves, feet, itemId, itemCount));
        }
    }
}