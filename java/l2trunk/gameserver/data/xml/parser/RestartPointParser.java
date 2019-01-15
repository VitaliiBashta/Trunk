package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.geometry.Rectangle;
import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.templates.mapregion.RestartArea;
import l2trunk.gameserver.templates.mapregion.RestartPoint;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum RestartPointParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/mapregion/restart_points.xml");

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + MapRegionHolder.size() + " items ");
    }

    private void readData(Element rootElement) {
        List<Pair<Territory, Map<Race, String>>> restartArea = new ArrayList<>();
        Map<String, RestartPoint> restartPoint = new HashMap<>();

        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element listElement = iterator.next();

            if ("restart_area".equals(listElement.getName())) {
                Territory territory = null;
                Map<Race, String> restarts = new HashMap<>();

                for (Iterator<Element> i = listElement.elementIterator(); i.hasNext(); ) {
                    Element n = i.next();

                    if ("region".equalsIgnoreCase(n.getName())) {
                        Rectangle shape;

                        Attribute map = n.attribute("map");
                        String s = map.getValue();
                        String val[] = s.split("_");
                        int rx = toInt(val[0]);
                        int ry = toInt(val[1]);

                        int x1 = World.MAP_MIN_X + (rx - Config.GEO_X_FIRST << 15);
                        int y1 = World.MAP_MIN_Y + (ry - Config.GEO_Y_FIRST << 15);
                        int x2 = x1 + (1 << 15) - 1;
                        int y2 = y1 + (1 << 15) - 1;

                        shape = new Rectangle(x1, y1, x2, y2);
                        shape.setZmin(World.MAP_MIN_Z);
                        shape.setZmax(World.MAP_MAX_Z);

                        if (territory == null)
                            territory = new Territory();

                        territory.add(shape);
                    } else if ("polygon".equalsIgnoreCase(n.getName())) {
                        Polygon shape = ZoneParser.parsePolygon(n);

                        if (!shape.validate())
                            LOG.error("RestartPointParser: invalid territory data : " + shape + "!");

                        if (territory == null)
                            territory = new Territory();

                        territory.add(shape);
                    } else if ("restart".equalsIgnoreCase(n.getName())) {
                        Race race = Race.valueOf(n.attributeValue("race"));
                        String locName = n.attributeValue("loc");
                        restarts.put(race, locName);
                    }
                }

                if (territory == null)
                    throw new RuntimeException("RestartPointParser: empty territory!");

                if (restarts.isEmpty())
                    throw new RuntimeException("RestartPointParser: restarts not defined!");

                restartArea.add(new Pair<>(territory, restarts));
            } else if ("restart_loc".equals(listElement.getName())) {
                String name = listElement.attributeValue("name");
                int bbs = toInt(listElement.attributeValue("bbs", "0"));
                int msgId = toInt(listElement.attributeValue("msg_id", "0"));
                List<Location> restartPoints = new ArrayList<>();
                List<Location> PKrestartPoints = new ArrayList<>();

                for (Iterator<Element> i = listElement.elementIterator(); i.hasNext(); ) {
                    Element n = i.next();
                    if ("restart_point".equals(n.getName())) {
                        for (Iterator<Element> ii = n.elementIterator(); ii.hasNext(); ) {
                            Element d = ii.next();
                            if ("coords".equalsIgnoreCase(d.getName())) {
                                Location loc = Location.parseLoc(d.attribute("loc").getValue());
                                restartPoints.add(loc);
                            }
                        }
                    } else if ("PKrestart_point".equals(n.getName())) {
                        for (Iterator<Element> ii = n.elementIterator(); ii.hasNext(); ) {
                            Element d = ii.next();
                            if ("coords".equalsIgnoreCase(d.getName())) {
                                Location loc = Location.parseLoc(d.attribute("loc").getValue());
                                PKrestartPoints.add(loc);
                            }
                        }
                    }
                }

                if (restartPoints.isEmpty())
                    throw new RuntimeException("RestartPointParser: restart_points not defined for restart_loc : " + name + "!");

                if (PKrestartPoints.isEmpty())
                    PKrestartPoints = restartPoints;

                RestartPoint rp = new RestartPoint(name, bbs, msgId, restartPoints, PKrestartPoints);
                restartPoint.put(name, rp);
            }
        }

        for (Pair<Territory, Map<Race, String>> ra : restartArea) {
            Map<Race, RestartPoint> restarts = new HashMap<>();

            for (Map.Entry<Race, String> e : ra.getValue().entrySet()) {
                RestartPoint rp = restartPoint.get(e.getValue());
                if (rp == null)
                    throw new RuntimeException("RestartPointParser: restart_loc not found : " + e.getValue() + "!");

                restarts.put(e.getKey(), rp);

                MapRegionHolder.addRegionData(new RestartArea(ra.getKey(), restarts));
            }
        }
    }
}
