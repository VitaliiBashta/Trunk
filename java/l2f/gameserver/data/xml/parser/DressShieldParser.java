package l2f.gameserver.data.xml.parser;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.DressShieldHolder;
import l2f.gameserver.model.DressShieldData;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;

public final class DressShieldParser extends AbstractFileParser<DressShieldHolder> {
    private static final DressShieldParser _instance = new DressShieldParser();

    private DressShieldParser() {
        super(DressShieldHolder.getInstance());
    }

    public static DressShieldParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/dress/shield.xml");
    }

    @Override
    public String getDTDFileName() {
        return "shield.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("shield"); iterator.hasNext(); ) {
            String name = null;
            int id, number, itemId;
            long itemCount;
            Element dress = iterator.next();
            number = Integer.parseInt(dress.attributeValue("number"));
            id = Integer.parseInt(dress.attributeValue("id"));
            name = dress.attributeValue("name");

            Element price = dress.element("price");
            itemId = Integer.parseInt(price.attributeValue("id"));
            itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addShield(new DressShieldData(number, id, name, itemId, itemCount));
        }
    }
}