package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.DoorHolder;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.data.xml.holder.SpawnHolder;
import l2trunk.gameserver.data.xml.holder.ZoneHolder;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.templates.DoorTemplate;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.templates.InstantZone.SpawnInfo;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum InstantZoneParser {
    INSTANCE;
    private final Path xml = Config.DATAPACK_ROOT.resolve("data/instances/");
    private Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + InstantZoneHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            int instanceId;
            String name;
            SchedulingPattern resetReuse = new SchedulingPattern("30 6 * * *"); // Сброс реюза по умолчанию в каждые сутки в 6:30
            int timelimit = -1;
            int timer = 60;
            int mapx = -1;
            int mapy = -1;
            boolean dispelBuffs;
            boolean onPartyDismiss = true;
            int mobId, respawn, respawnRnd, count, sharedReuseGroup = 0;
            int collapseIfEmpty;
            // 0 - точечный, в каждой указанной точке; 1 - один точечный спаун в рандомной точке; 2 - локационный
            int spawnType;
            SpawnInfo spawnDat;
            int removedItemId = 0, removedItemCount = 0, giveItemId = 0, givedItemCount = 0, requiredQuestId = 0;
            int maxChannels;
            boolean removedItemNecessity = false;
            boolean setReuseUponEntry = true;
            StatsSet params = new StatsSet();

            List<InstantZone.SpawnInfo> spawns = new ArrayList<>();
            Map<Integer, InstantZone.DoorInfo> doors =  new ConcurrentHashMap<>();
            Map<String, InstantZone.ZoneInfo> zones =  new ConcurrentHashMap<>();
            Map<String, InstantZone.SpawnInfo2> spawns2 = new ConcurrentHashMap<>();
            instanceId = toInt(element.attributeValue("id"));
            name = element.attributeValue("name");

            String n = element.attributeValue("timelimit");
            if (n != null)
                timelimit = toInt(n);

            n = element.attributeValue("collapseIfEmpty");
            collapseIfEmpty = toInt(n);

            n = element.attributeValue("maxChannels");
            maxChannels = toInt(n);

            n = element.attributeValue("dispelBuffs");
            dispelBuffs = Boolean.parseBoolean(n);

            int minLevel = 0, maxLevel = 0, minParty = 1, maxParty = 9;
            List<Location> teleportLocs = Collections.emptyList();
            Location ret = null;

            for (Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();

                if ("level".equalsIgnoreCase(subElement.getName())) {
                    if (Config.ALLOW_INSTANCES_LEVEL_MANUAL) {
                        minLevel = Config.INSTANCES_LEVEL_MIN;
                        maxLevel = Config.INSTANCES_LEVEL_MAX;
                    } else {
                        minLevel = toInt(subElement.attributeValue("min"));
                        maxLevel = toInt(subElement.attributeValue("max"));
                    }
                } else if ("collapse".equalsIgnoreCase(subElement.getName())) {
                    onPartyDismiss = Boolean.parseBoolean(subElement.attributeValue("on-party-dismiss"));
                    timer = toInt(subElement.attributeValue("timer"));
                } else if ("party".equalsIgnoreCase(subElement.getName())) {
                    if (Config.ALLOW_INSTANCES_PARTY_MANUAL) {
                        minParty = Config.INSTANCES_PARTY_MIN;
                        maxParty = Config.INSTANCES_PARTY_MAX;
                    } else {
                        minParty = toInt(subElement.attributeValue("min"));
                        maxParty = toInt(subElement.attributeValue("max"));
                    }
                } else if ("return".equalsIgnoreCase(subElement.getName()))
                    ret = Location.of(subElement.attributeValue("loc"));
                else if ("teleport".equalsIgnoreCase(subElement.getName())) {
                    if (teleportLocs.isEmpty())
                        teleportLocs = new ArrayList<>(1);
                    teleportLocs.add(Location.of(subElement.attributeValue("loc")));
                } else if ("remove".equalsIgnoreCase(subElement.getName())) {
                    removedItemId = toInt(subElement.attributeValue("itemId"));
                    removedItemCount = toInt(subElement.attributeValue("count"));
                    removedItemNecessity = Boolean.parseBoolean(subElement.attributeValue("necessary"));
                } else if ("give".equalsIgnoreCase(subElement.getName())) {
                    giveItemId = toInt(subElement.attributeValue("itemId"));
                    givedItemCount = toInt(subElement.attributeValue("count"));
                } else if ("quest".equalsIgnoreCase(subElement.getName())) {
                    requiredQuestId = toInt(subElement.attributeValue("id"));
                } else if ("reuse".equalsIgnoreCase(subElement.getName())) {
                    resetReuse = new SchedulingPattern(subElement.attributeValue("resetReuse"));
                    sharedReuseGroup = toInt(subElement.attributeValue("sharedReuseGroup"));
                    setReuseUponEntry = Boolean.parseBoolean(subElement.attributeValue("setUponEntry"));
                } else if ("geodata".equalsIgnoreCase(subElement.getName())) {
                    String[] rxy = subElement.attributeValue("map").split("_");
                    mapx = toInt(rxy[0]);
                    mapy = toInt(rxy[1]);
                } else if ("doors".equalsIgnoreCase(subElement.getName())) {
                    for (Element e : subElement.elements()) {
                        boolean opened = e.attributeValue("opened") != null && Boolean.parseBoolean(e.attributeValue("opened"));
                        boolean invul = e.attributeValue("invul") == null || Boolean.parseBoolean(e.attributeValue("invul"));
                        DoorTemplate template = DoorHolder.getTemplate(toInt(e.attributeValue("id")));
                        doors.put(template.getNpcId(), new InstantZone.DoorInfo(template, opened, invul));
                    }
                } else if ("zones".equalsIgnoreCase(subElement.getName())) {
                    for (Element e : subElement.elements()) {

                        boolean active = e.attributeValue("active") != null && Boolean.parseBoolean(e.attributeValue("active"));
                        ZoneTemplate template = ZoneHolder.getTemplate(e.attributeValue("name"));
                        if (template == null) {
                            LOG.error("Zone: " + e.attributeValue("name") + " not found; file: ");
                            continue;
                        }
                        zones.put(template.getName(), new InstantZone.ZoneInfo(template, active));
                    }
                } else if ("add_parameters".equalsIgnoreCase(subElement.getName())) {
                    for (Element e : subElement.elements())
                        if ("param".equalsIgnoreCase(e.getName()))
                            params.set(e.attributeValue("name"), e.attributeValue("value"));
                } else if ("spawns".equalsIgnoreCase(subElement.getName())) {
                    for (Element e : subElement.elements())
                        if ("group".equalsIgnoreCase(e.getName())) {
                            String group = e.attributeValue("name");
                            boolean spawned = e.attributeValue("spawned") != null && Boolean.parseBoolean(e.attributeValue("spawned"));
                            List<SpawnTemplate> templates = SpawnHolder.getSpawn(group);

                            spawns2.put(group, new InstantZone.SpawnInfo2(templates, spawned));
                        } else if ("spawn".equalsIgnoreCase(e.getName())) {
                            String[] mobs = e.attributeValue("mobId").split(" ");

                            String respawnNode = e.attributeValue("respawn");
                            respawn = respawnNode != null ? toInt(respawnNode) : 0;

                            String respawnRndNode = e.attributeValue("respawnRnd");
                            respawnRnd = respawnRndNode != null ? toInt(respawnRndNode) : 0;

                            String countNode = e.attributeValue("count");
                            count = countNode != null ? toInt(countNode) : 1;

                            List<Location> coords = new ArrayList<>();
                            spawnType = 0;

                            String spawnTypeNode = e.attributeValue("type");
                            if (spawnTypeNode == null || "point".equalsIgnoreCase(spawnTypeNode))
                                spawnType = 0;
                            else if ("rnd".equalsIgnoreCase(spawnTypeNode))
                                spawnType = 1;
                            else if ("loc".equalsIgnoreCase(spawnTypeNode))
                                spawnType = 2;
                            else
                                LOG.error("Spawn type  '" + spawnTypeNode + "' is unknown!");

                            for (Element e2 : e.elements())
                                if ("coords".equalsIgnoreCase(e2.getName()))
                                    coords.add(Location.of(e2.attributeValue("loc")));

                            Territory territory = null;
                            if (spawnType == 2) {
                                Polygon poly = new Polygon();
                                coords.forEach(loc ->
                                    poly.add(loc.x, loc.y).setZmin(loc.z).setZmax(loc.z));

                                if (!poly.validate())
                                    LOG.error("invalid spawn territory for instance id : " + instanceId + " - " + poly + "!");

                                territory = new Territory().add(poly);
                            }

                            for (String mob : mobs) {
                                mobId = toInt(mob);
                                spawnDat = new InstantZone.SpawnInfo(spawnType, mobId, count, respawn, respawnRnd, coords, territory);
                                spawns.add(spawnDat);
                            }
                        }
                }
            }

            InstantZone instancedZone = new InstantZone(instanceId, name, resetReuse, sharedReuseGroup, timelimit, dispelBuffs, minLevel, maxLevel, minParty, maxParty, timer, onPartyDismiss, teleportLocs, ret, mapx, mapy, doors, zones, spawns2, spawns, collapseIfEmpty, maxChannels, removedItemId, removedItemCount, removedItemNecessity, giveItemId, givedItemCount, requiredQuestId, setReuseUponEntry, params);
            InstantZoneHolder.addInstantZone(instancedZone);
        }
    }
}