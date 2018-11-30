package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.AirshipDockHolder;
import l2trunk.gameserver.model.entity.events.objects.BoatPoint;
import l2trunk.gameserver.network.serverpackets.components.SceneMovie;
import l2trunk.gameserver.templates.AirshipDock;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class AirshipDockParser extends AbstractFileParser<AirshipDockHolder> {
    private static final AirshipDockParser _instance = new AirshipDockParser();

    private AirshipDockParser() {
        super(AirshipDockHolder.getInstance());
    }

    public static AirshipDockParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/airship_docks.xml");
    }

    @Override
    public String getDTDFileName() {
        return "airship_docks.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element dockElement = iterator.next();
            int id = Integer.parseInt(dockElement.attributeValue("id"));

            List<BoatPoint> teleportList = parsePoints(dockElement.element("teleportlist"));

            for (BoatPoint point : teleportList) {
                point.setTeleport(true);
                point.setSpeed1(-1);
                point.setSpeed2(-1);
            }

            List<AirshipDock.AirshipPlatform> platformList = new ArrayList<>(2);
            for (Iterator<Element> platformIterator = dockElement.elementIterator("platform"); platformIterator.hasNext(); ) {
                Element platformElement = platformIterator.next();
                SceneMovie movie = SceneMovie.valueOf(platformElement.attributeValue("movie"));
                BoatPoint oustLoc = BoatPoint.parse(platformElement.element("oust"));
                BoatPoint spawnLoc = BoatPoint.parse(platformElement.element("spawn"));
                List<BoatPoint> arrivalList = parsePoints(platformElement.element("arrival"));
                List<BoatPoint> departList = parsePoints(platformElement.element("depart"));

                AirshipDock.AirshipPlatform platform = new AirshipDock.AirshipPlatform(movie, oustLoc, spawnLoc, arrivalList, departList);
                platformList.add(platform);
            }

            getHolder().addDock(new AirshipDock(id, teleportList, platformList));
        }
    }

    private List<BoatPoint> parsePoints(Element listElement) {
        if (listElement == null)
            return Collections.emptyList();
        List<BoatPoint> list = new ArrayList<>(5);
        for (Iterator<Element> iterator = listElement.elementIterator(); iterator.hasNext(); )
            list.add(BoatPoint.parse(iterator.next()));

        return list.isEmpty() ? Collections.<BoatPoint>emptyList() : list;
    }
}
