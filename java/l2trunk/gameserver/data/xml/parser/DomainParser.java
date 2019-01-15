package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Polygon;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.templates.mapregion.DomainArea;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

public enum DomainParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/mapregion/domains.xml");

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + MapRegionHolder.size() + " items ");
    }

    private void readData(Element rootElement) {
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
                            LOG.error("DomainParser: invalid territory data : " + shape + "!");

                        if (territory == null)
                            territory = new Territory();

                        territory.add(shape);
                    }
                }

                if (territory == null)
                    throw new RuntimeException("DomainParser: empty territory!");

                MapRegionHolder.addRegionData(new DomainArea(id, territory));
            }
        }
    }
}
