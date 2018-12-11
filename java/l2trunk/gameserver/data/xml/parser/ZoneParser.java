package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Circle;
import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.geometry.Rectangle;
import l2trunk.commons.geometry.Shape;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ZoneHolder;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.templates.StatsSet;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum ZoneParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/zone/");

    private static Rectangle parseRectangle(Element n) {
        int x1, y1, x2, y2, zmin = World.MAP_MIN_Z, zmax = World.MAP_MAX_Z;

        Iterator<Element> i = n.elementIterator();

        Element d = i.next();
        String[] coord = d.attributeValue("loc").split("[\\s,;]+");
        x1 = Integer.parseInt(coord[0]);
        y1 = Integer.parseInt(coord[1]);
        if (coord.length > 2) {
            zmin = Integer.parseInt(coord[2]);
            zmax = Integer.parseInt(coord[3]);
        }

        d = i.next();
        coord = d.attributeValue("loc").split("[\\s,;]+");
        x2 = Integer.parseInt(coord[0]);
        y2 = Integer.parseInt(coord[1]);
        if (coord.length > 2) {
            zmin = Integer.parseInt(coord[2]);
            zmax = Integer.parseInt(coord[3]);
        }

        Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
        rectangle.setZmin(zmin);
        rectangle.setZmax(zmax);

        return rectangle;
    }

    static Polygon parsePolygon(Element shape) {
        Polygon poly = new Polygon();

        for (Iterator<Element> i = shape.elementIterator(); i.hasNext(); ) {
            Element d = i.next();
            if ("coords".equals(d.getName())) {
                String[] coord = d.attributeValue("loc").split("[\\s,;]+");
                if (coord.length < 4) // Не указаны minZ и maxZ, берем граничные значения
                    poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
                else
                    poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(Integer.parseInt(coord[3]));
            }
        }

        return poly;
    }

    private Circle parseCircle(Element shape) {
        Circle circle;

        String[] coord = shape.attribute("loc").getValue().split("[\\s,;]+");
        if (coord.length < 5) // Не указаны minZ и maxZ, берем граничные значения
            circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
        else
            circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(Integer.parseInt(coord[3])).setZmax(Integer.parseInt(coord[4]));

        return circle;
    }

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + ZoneHolder.size() + " items ");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            StatsSet zoneDat = new StatsSet();
            Element zoneElement = iterator.next();

            if ("zone".equals(zoneElement.getName())) {
                zoneDat.set("name", zoneElement.attribute("name").getValue());
                zoneDat.set("type", zoneElement.attribute("type").getValue());

                Territory territory = null;
                boolean isShape;

                for (Iterator<Element> i = zoneElement.elementIterator(); i.hasNext(); ) {
                    Element n = i.next();
                    if ("set".equals(n.getName()))
                        zoneDat.set(n.attributeValue("name"), n.attributeValue("val"));
                    else if ("restart_point".equals(n.getName())) {
                        List<Location> restartPoints = new ArrayList<>();
                        for (Iterator<Element> ii = n.elementIterator(); ii.hasNext(); ) {
                            Element d = ii.next();
                            if ("coords".equalsIgnoreCase(d.getName())) {
                                Location loc = Location.parseLoc(d.attribute("loc").getValue());
                                restartPoints.add(loc);
                            }
                        }
                        zoneDat.set("restart_points", restartPoints);
                    } else if ("PKrestart_point".equals(n.getName())) {
                        List<Location> PKrestartPoints = new ArrayList<>();
                        for (Iterator<Element> ii = n.elementIterator(); ii.hasNext(); ) {
                            Element d = ii.next();
                            if ("coords".equalsIgnoreCase(d.getName())) {
                                Location loc = Location.parseLoc(d.attribute("loc").getValue());
                                PKrestartPoints.add(loc);
                            }
                        }
                        zoneDat.set("PKrestart_points", PKrestartPoints);
                    } else if ((isShape = "rectangle".equalsIgnoreCase(n.getName())) || "banned_rectangle".equalsIgnoreCase(n.getName())) {
                        Shape shape = parseRectangle(n);

                        if (territory == null) {
                            territory = new Territory();
                            zoneDat.set("territory", territory);
                        }

                        if (isShape)
                            territory.add(shape);
                        else
                            territory.addBanned(shape);
                    } else if ((isShape = "circle".equalsIgnoreCase(n.getName())) || "banned_cicrcle".equalsIgnoreCase(n.getName())) {
                        Shape shape = parseCircle(n);

                        if (territory == null) {
                            territory = new Territory();
                            zoneDat.set("territory", territory);
                        }

                        if (isShape)
                            territory.add(shape);
                        else
                            territory.addBanned(shape);
                    } else if ((isShape = "polygon".equalsIgnoreCase(n.getName())) || "banned_polygon".equalsIgnoreCase(n.getName())) {
                        Polygon shape = parsePolygon(n);

                        if (!shape.validate())
                            LOG.error("ZoneParser: invalid territory data : " + shape + ", zone: " + zoneDat.getString("name") + "!");

                        if (territory == null) {
                            territory = new Territory();
                            zoneDat.set("territory", territory);
                        }

                        if (isShape)
                            territory.add(shape);
                        else
                            territory.addBanned(shape);
                    }
                }

                if (territory == null || territory.getTerritories().isEmpty())
                    LOG.error("Empty territory for zone: " + zoneDat.get("name"));
                ZoneTemplate template = new ZoneTemplate(zoneDat);
                ZoneHolder.addTemplate(template);
            }
        }
    }
}