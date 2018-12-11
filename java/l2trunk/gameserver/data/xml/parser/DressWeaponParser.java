package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressWeaponHolder;
import l2trunk.gameserver.model.DressWeaponData;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum DressWeaponParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/dress/weapon.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + DressWeaponHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("weapon"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int id = toInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");
            String type = dress.attributeValue("type");

            Element price = dress.element("price");
            int itemId = toInt(price.attributeValue("id"));
            long itemCount = toInt(price.attributeValue("count"));

            DressWeaponHolder.addWeapon(new DressWeaponData(id, name, type, itemId, itemCount));
        }
    }
}