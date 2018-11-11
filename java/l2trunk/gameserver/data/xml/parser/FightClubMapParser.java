package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.commons.data.xml.AbstractDirParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.FightClubMapHolder;
import l2trunk.gameserver.data.xml.holder.ZoneHolder;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubMap;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.*;

public final class FightClubMapParser extends AbstractDirParser<FightClubMapHolder> {
    private static final FightClubMapParser _instance = new FightClubMapParser();

    private FightClubMapParser() {
        super(FightClubMapHolder.getInstance());
    }

    public static FightClubMapParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLDir() {
        return Config.DATAPACK_ROOT.resolve("data/fight_club_maps/");
    }

    @Override
    public String getDTDFileName() {
        return "maps.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("map"); iterator.hasNext(); ) {
            Element eventElement = iterator.next();
            String name = eventElement.attributeValue("name");

            MultiValueSet<String> set = new MultiValueSet<>();
            set.set("name", name);

            for (Iterator<Element> parameterIterator = eventElement.elementIterator("parameter"); parameterIterator.hasNext(); ) {
                Element parameterElement = parameterIterator.next();
                set.set(parameterElement.attributeValue("name"), parameterElement.attributeValue("value"));
            }

            Map<Integer, Location[]> teamSpawns = null;
            Map<Integer, Map<String, ZoneTemplate>> territories = null;
            Map<Integer, Map<Integer, Location[]>> npcWaypath = null;
            Location[] keyLocations = null;

            for (Iterator<Element> objectIterator = eventElement.elementIterator("objects"); objectIterator.hasNext(); ) {
                Element objectElement = objectIterator.next();
                String objectsName = objectElement.attributeValue("name");

                int team = Integer.parseInt(objectElement.attributeValue("team", "-1"));
                int index = Integer.parseInt(objectElement.attributeValue("index", "-1"));

                switch (objectsName) {
                    case "teamSpawns":
                        if (teamSpawns == null)
                            teamSpawns = new HashMap<>();
                        teamSpawns.put(team, parseLocations(objectElement));
                        break;
                    case "territory":
                        if (territories == null)
                            territories = new HashMap<>();
                        territories.put(team, parseTerritory(objectElement));
                        break;
                    case "npcWaypath":
                        if (npcWaypath == null)
                            npcWaypath = new HashMap<>();

                        npcWaypath.computeIfAbsent(team, k -> new HashMap<>());

                        npcWaypath.get(team).put(index, parseLocations(objectElement));
                        break;
                    case "keyLocations":
                        keyLocations = parseLocations(objectElement);
                        break;
                }
            }

            getHolder().addMap(new FightClubMap(set, teamSpawns, territories, npcWaypath, keyLocations));
        }
    }

    private Location[] parseLocations(Element element) {
        List<Location> locs = new ArrayList<>();
        for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext(); ) {
            Element objectsElement = objectsIterator.next();
            final String nodeName = objectsElement.getName();

            if (nodeName.equalsIgnoreCase("point"))
                locs.add(Location.parse(objectsElement));
        }

        Location[] locArray = new Location[locs.size()];

        for (int i = 0; i < locs.size(); i++)
            locArray[i] = locs.get(i);

        return locArray;
    }

    private Map<String, ZoneTemplate> parseTerritory(Element element) {
        Map<String, ZoneTemplate> territories = new HashMap<>();
        for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext(); ) {
            Element objectsElement = objectsIterator.next();
            final String nodeName = objectsElement.getName();

            if (nodeName.equalsIgnoreCase("zone")) {
                ZoneTemplate template = ZoneHolder.getInstance().getTemplate(objectsElement.attributeValue("name"));
                territories.put(template.getName(), template);
            }
        }

        return territories;
    }
}
