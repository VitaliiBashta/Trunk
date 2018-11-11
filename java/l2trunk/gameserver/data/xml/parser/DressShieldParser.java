package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressShieldHolder;
import l2trunk.gameserver.model.DressShieldData;
import org.dom4j.Element;

import java.nio.file.Path;
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
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/dress/shield.xml");
    }

    @Override
    public String getDTDFileName() {
        return "shield.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("shield"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int number = Integer.parseInt(dress.attributeValue("number"));
            int id = Integer.parseInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");

            Element price = dress.element("price");
            int itemId = Integer.parseInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addShield(new DressShieldData(number, id, name, itemId, itemCount));
        }
    }
}