package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Polygon;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DoorHolder;
import l2trunk.gameserver.templates.DoorTemplate;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum DoorParser {
    INSTANCE;
    private final Path xml = Config.DATAPACK_ROOT.resolve("data/doors/allDoors.xml");
    private Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + DoorHolder.size() + " items");
    }

    private StatsSet initBaseStats() {
        StatsSet baseDat = new StatsSet();
        baseDat.set("baseAccCombat", 38);
        baseDat.set("baseEvasRate", 38);
        baseDat.set("baseCritRate", 38);
        return baseDat;
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element doorElement = iterator.next();

            if ("door".equals(doorElement.getName())) {
                StatsSet doorSet = initBaseStats();
                StatsSet aiParams = null;

                doorSet.set("door_type", doorElement.attributeValue("type"));

                Element posElement = doorElement.element("pos");
                Location doorPos;
                int x = toInt(posElement.attributeValue("x"));
                int y = toInt(posElement.attributeValue("y"));
                int z = toInt(posElement.attributeValue("z"));
                doorSet.set("pos", doorPos = Location.of(x, y, z));

                Polygon shape = new Polygon();
                int minz, maxz;

                Element shapeElement = doorElement.element("shape");
                minz = toInt(shapeElement.attributeValue("minz"));
                maxz = toInt(shapeElement.attributeValue("maxz"));
                shape.add(toInt(shapeElement.attributeValue("ax")), toInt(shapeElement.attributeValue("ay")));
                shape.add(toInt(shapeElement.attributeValue("bx")), toInt(shapeElement.attributeValue("by")));
                shape.add(toInt(shapeElement.attributeValue("cx")), toInt(shapeElement.attributeValue("cy")));
                shape.add(toInt(shapeElement.attributeValue("dx")), toInt(shapeElement.attributeValue("dy")));
                shape.setZmin(minz);
                shape.setZmax(maxz);
                doorSet.set("shape", shape);

                doorPos.setZ(minz + 32); //фактическая координата двери в мире

                for (Iterator<Element> i = doorElement.elementIterator(); i.hasNext(); ) {
                    Element n = i.next();
                    if ("set".equals(n.getName()))
                        doorSet.set(n.attributeValue("name"), n.attributeValue("value"));
                    else if ("ai_params".equals(n.getName())) {
                        if (aiParams == null) {
                            aiParams = new StatsSet();
                            doorSet.set("ai_params", aiParams);
                        }

                        for (Iterator<Element> aiParamsIterator = n.elementIterator(); aiParamsIterator.hasNext(); ) {
                            Element aiParamElement = aiParamsIterator.next();

                            aiParams.set(aiParamElement.attributeValue("name"), aiParamElement.attributeValue("value"));
                        }
                    }
                }

                doorSet.set("uid", doorElement.attributeValue("id"))
                        .set("name", doorElement.attributeValue("name"))
                        .set("baseHpMax", doorElement.attributeValue("hp"))
                        .set("basePDef", doorElement.attributeValue("pdef"))
                        .set("baseMDef", doorElement.attributeValue("mdef"))

                        .set("collision_height", maxz - minz & 0xfff0)
                        .set("collision_radius", Math.max(50, Math.min(doorPos.x - shape.getXmin(), doorPos.y - shape.getYmin())));

                DoorTemplate template = new DoorTemplate(doorSet);
                DoorHolder.addTemplate(template);
            }
        }
    }
}
