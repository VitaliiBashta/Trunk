package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressShieldHolder;
import l2trunk.gameserver.model.DressShieldData;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum DressShieldParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/dress/shield.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + DressShieldHolder.size() + " items ");
    }


    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("shield"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int number = toInt(dress.attributeValue("number"));
            int id = toInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");

            Element price = dress.element("price");
            int itemId = toInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            DressShieldHolder.addShield(new DressShieldData(number, id, name, itemId, itemCount));
        }
    }
}