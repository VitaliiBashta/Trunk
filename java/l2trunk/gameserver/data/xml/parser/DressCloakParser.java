package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressCloakHolder;
import l2trunk.gameserver.model.DressCloakData;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum DressCloakParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/dress/cloak.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + DressCloakHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("cloak"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int number = toInt(dress.attributeValue("number"));
            int id = toInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");

            Element price = dress.element("price");
            int itemId = toInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            DressCloakHolder.addCloak(new DressCloakData(number, id, name, itemId, itemCount));
        }
    }
}