package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.commons.data.xml.AbstractDirParser;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Polygon;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.SpawnHolder;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.templates.StatsSet;
import l2trunk.gameserver.templates.spawn.PeriodOfDay;
import l2trunk.gameserver.templates.spawn.SpawnNpcInfo;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum  SpawnParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/spawn/");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + SpawnHolder.size() + " items");
    }
    
    private void readData(Element rootElement) {
        Map<String, Territory> territories = new HashMap<>();
        for (Iterator<Element> spawnIterator = rootElement.elementIterator(); spawnIterator.hasNext(); ) {
            Element spawnElement = spawnIterator.next();
            if (spawnElement.getName().equalsIgnoreCase("territory")) {
                String terName = spawnElement.attributeValue("name");
                Territory territory = parseTerritory(terName, spawnElement);
                territories.put(terName, territory);
            } else if (spawnElement.getName().equalsIgnoreCase("spawn")) {
                String group = spawnElement.attributeValue("group");
                int respawn = spawnElement.attributeValue("respawn") == null ? 60 : toInt(spawnElement.attributeValue("respawn"));
                int respawnRandom = spawnElement.attributeValue("respawn_random") == null ? 0 : toInt(spawnElement.attributeValue("respawn_random"));
                int count = spawnElement.attributeValue("count") == null ? 1 : toInt(spawnElement.attributeValue("count"));
                PeriodOfDay periodOfDay = spawnElement.attributeValue("period_of_day") == null ? PeriodOfDay.NONE : PeriodOfDay.valueOf(spawnElement.attributeValue("period_of_day").toUpperCase());
                if (group == null)
                    group = periodOfDay.name();
                SpawnTemplate template = new SpawnTemplate(periodOfDay, count, respawn, respawnRandom);

                for (Iterator<Element> subIterator = spawnElement.elementIterator(); subIterator.hasNext(); ) {
                    Element subElement = subIterator.next();
                    if (subElement.getName().equalsIgnoreCase("point")) {
                        int x = toInt(subElement.attributeValue("x"));
                        int y = toInt(subElement.attributeValue("y"));
                        int z = toInt(subElement.attributeValue("z"));
                        int h = subElement.attributeValue("h") == null ? -1 : toInt(subElement.attributeValue("h"));

                        template.addSpawnRange(new Location(x, y, z, h));
                    } else if (subElement.getName().equalsIgnoreCase("territory")) {
                        String terName = subElement.attributeValue("name");
                        if (terName != null) {
                            Territory g = territories.get(terName);
                            if (g == null) {
                                LOG.error("Invalid territory name: " + terName);
                                continue;
                            }
                            template.addSpawnRange(g);
                        } else {
                            Territory temp = parseTerritory(null, subElement);

                            template.addSpawnRange(temp);
                        }
                    } else if (subElement.getName().equalsIgnoreCase("npc")) {
                        int npcId = toInt(subElement.attributeValue("id"));
                        int max = subElement.attributeValue("max") == null ? 0 : toInt(subElement.attributeValue("max"));
                        MultiValueSet<String> parameters = StatsSet.EMPTY;
                        for (Element e : subElement.elements()) {
                            if (parameters.isEmpty())
                                parameters = new MultiValueSet<>();

                            parameters.set(e.attributeValue("name"), e.attributeValue("value"));
                        }
                        template.addNpc(new SpawnNpcInfo(npcId, max, parameters));
                    }
                }

                if (template.getNpcSize() == 0) {
                    LOG.warn("Npc id is zero! in template : ");
                    continue;
                }

                if (template.getSpawnRangeSize() == 0) {
                    LOG.warn("No points to spawn! File: ");
                    continue;
                }

                SpawnHolder.addSpawn(group, template);
            }
        }
    }

    private Territory parseTerritory(String name, Element e) {
        Territory t = new Territory();
        t.add(parsePolygon0(name, e));

        for (Iterator<Element> iterator = e.elementIterator("banned_territory"); iterator.hasNext(); )
            t.addBanned(parsePolygon0(name, iterator.next()));

        return t;
    }

    private Polygon parsePolygon0(String name, Element e) {
        Polygon temp = new Polygon();
        for (Iterator<Element> addIterator = e.elementIterator("add"); addIterator.hasNext(); ) {
            Element addElement = addIterator.next();
            int x = toInt(addElement.attributeValue("x"));
            int y = toInt(addElement.attributeValue("y"));
            int zmin = toInt(addElement.attributeValue("zmin"));
            int zmax = toInt(addElement.attributeValue("zmax"));
            temp.add(x, y).setZmin(zmin).setZmax(zmax);
        }

        if (!temp.validate())
            LOG.error("Invalid polygon: " + name + "{" + temp + "}. File: ");
        return temp;
    }
}
