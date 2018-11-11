package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.commons.geometry.Polygon;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.MapRegionManager;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.templates.mapregion.DomainArea;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class DomainParser extends AbstractFileParser<MapRegionManager> {
    private static final DomainParser _instance = new DomainParser();

    private DomainParser() {
        super(MapRegionManager.getInstance());
    }

    public static DomainParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/mapregion/domains.xml");
    }

    @Override
    public String getDTDFileName() {
        return "domains.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element listElement = iterator.next();

            if ("domain".equals(listElement.getName())) {
                int id = Integer.parseInt(listElement.attributeValue("id"));
                Territory territory = null;

                for (Iterator<Element> i = listElement.elementIterator(); i.hasNext(); ) {
                    Element n = i.next();

                    if ("polygon".equalsIgnoreCase(n.getName())) {
                        Polygon shape = ZoneParser.parsePolygon(n);

                        if (!shape.validate())
                            error("DomainParser: invalid territory data : " + shape + "!");

                        if (territory == null)
                            territory = new Territory();

                        territory.add(shape);
                    }
                }

                if (territory == null)
                    throw new RuntimeException("DomainParser: empty territory!");

                getHolder().addRegionData(new DomainArea(id, territory));
            }
        }
    }
}
