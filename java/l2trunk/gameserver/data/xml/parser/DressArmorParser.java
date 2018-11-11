package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressArmorHolder;
import l2trunk.gameserver.model.DressArmorData;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class DressArmorParser extends AbstractFileParser<DressArmorHolder> {
    private static final DressArmorParser _instance = new DressArmorParser();

    private DressArmorParser() {
        super(DressArmorHolder.getInstance());
    }

    public static DressArmorParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/dress/armor.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armor.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("dress"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int id = Integer.parseInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");

            Element set = dress.element("set");

            int chest = Integer.parseInt(set.attributeValue("chest"));
            int legs = Integer.parseInt(set.attributeValue("legs"));
            int gloves = Integer.parseInt(set.attributeValue("gloves"));
            int feet = Integer.parseInt(set.attributeValue("feet"));

            Element price = dress.element("price");
            int itemId = Integer.parseInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addDress(new DressArmorData(id, name, chest, legs, gloves, feet, itemId, itemCount));
        }
    }
}