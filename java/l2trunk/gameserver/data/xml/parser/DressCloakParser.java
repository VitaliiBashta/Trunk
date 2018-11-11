package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DressCloakHolder;
import l2trunk.gameserver.model.DressCloakData;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class DressCloakParser extends AbstractFileParser<DressCloakHolder> {
    private static final DressCloakParser _instance = new DressCloakParser();

    private DressCloakParser() {
        super(DressCloakHolder.getInstance());
    }

    public static DressCloakParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/dress/cloak.xml");
    }

    @Override
    public String getDTDFileName() {
        return "cloak.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("cloak"); iterator.hasNext(); ) {
            Element dress = iterator.next();
            int number = Integer.parseInt(dress.attributeValue("number"));
            int id = Integer.parseInt(dress.attributeValue("id"));
            String name = dress.attributeValue("name");

            Element price = dress.element("price");
            int itemId = Integer.parseInt(price.attributeValue("id"));
            long itemCount = Long.parseLong(price.attributeValue("count"));

            getHolder().addCloak(new DressCloakData(number, id, name, itemId, itemCount));
        }
    }
}