package l2f.gameserver.data.xml.parser;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.DressArmorHolder;
import l2f.gameserver.model.DressArmorData;
import org.dom4j.Element;

import java.io.File;
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
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/dress/armor.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armor.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("dress"); iterator.hasNext(); ) {
            String name = null;
            int id, chest = 0, legs = 0, gloves = 0, feet = 0, itemId = 0;
            long itemCount = 0L;
            Element dress = iterator.next();
            id = Integer.parseInt(dress.attributeValue("id"));
            name = dress.attributeValue("name");

            Element set = dress.element("set");

            chest = Integer.parseInt(set.attributeValue("chest"));
            legs = Integer.parseInt(set.attributeValue("legs"));
            gloves = Integer.parseInt(set.attributeValue("gloves"));
            feet = Integer.parseInt(set.attributeValue("feet"));

            Element price = dress.element("price");
            itemId = Integer.parseInt(price.attributeValue("id"));
            itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addDress(new DressArmorData(id, name, chest, legs, gloves, feet, itemId, itemCount));
        }
    }
}