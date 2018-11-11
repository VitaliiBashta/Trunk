package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressWeaponHolder;
import l2trunk.gameserver.model.DressWeaponData;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class DressWeaponParser extends AbstractFileParser<DressWeaponHolder> {
    private static final DressWeaponParser _instance = new DressWeaponParser();

    private DressWeaponParser() {
        super(DressWeaponHolder.getInstance());
    }

    public static DressWeaponParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/dress/weapon.xml");
    }

    @Override
    public String getDTDFileName() {
        return "weapon.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("weapon"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int id = Integer.parseInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");
            String type = dress.attributeValue("type");

            Element price = dress.element("price");
            int itemId = Integer.parseInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addWeapon(new DressWeaponData(id, name, type, itemId, itemCount));
        }
    }
}