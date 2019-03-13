package l2trunk.gameserver.instancemanager;

import l2trunk.commons.geometry.Rectangle;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.TeleportToLocation;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DimensionalRiftManager {
    INSTANCE;
    private final static int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;
    private final Logger LOG = LoggerFactory.getLogger(DimensionalRiftManager.class);
    private final Map<Integer, Map<Integer, DimensionalRiftRoom>> rooms = new ConcurrentHashMap<>();

    public static void teleToLocation(Player player, Location loc, Reflection ref) {
        if (player.isTeleporting() || player.isDeleted())
            return;
        player.setIsTeleporting(true);

        player.setTarget(null);
        player.stopMove();

        if (player.isInBoat())
            player.setBoat(null);

        player.breakFakeDeath();

        player.decayMe();

        player.setLoc(loc);

        if (ref == null)
            player.setReflection(ReflectionManager.DEFAULT);

        // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
        player.setLastClientPosition(null);
        player.setLastServerPosition(null);
        player.sendPacket(new TeleportToLocation(player, loc));
    }

    public void init() {
        load();
    }

    public DimensionalRiftRoom getRoom(int type, int room) {
        return rooms.get(type).get(room);
    }

    public Map<Integer, DimensionalRiftRoom> getRooms(int type) {
        return rooms.get(type);
    }

    private void load() {
        int countGood = 0, countBad = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            Path file = Config.DATAPACK_ROOT.resolve("data/dimensional_rift.xml");
            if (!Files.exists(file))
                throw new IOException();

            Document doc = factory.newDocumentBuilder().parse(file.toFile());
            NamedNodeMap attrs;
            int type;
            int roomId;
            int mobId, delay, count;
            SimpleSpawner spawnDat;
            Location tele = new Location();
            int xMin, xMax, yMin, yMax, zMin, zMax;
            boolean isBossRoom;

            for (Node rift = doc.getFirstChild(); rift != null; rift = rift.getNextSibling())
                if ("rift".equalsIgnoreCase(rift.getNodeName()))
                    for (Node area = rift.getFirstChild(); area != null; area = area.getNextSibling())
                        if ("area".equalsIgnoreCase(area.getNodeName())) {
                            attrs = area.getAttributes();
                            type = Integer.parseInt(attrs.getNamedItem("type").getNodeValue());

                            for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
                                if ("room".equalsIgnoreCase(room.getNodeName())) {
                                    attrs = room.getAttributes();
                                    roomId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                                    Node boss = attrs.getNamedItem("isBossRoom");
                                    isBossRoom = boss != null && Boolean.parseBoolean(boss.getNodeValue());
                                    Territory territory = null;
                                    for (Node coord = room.getFirstChild(); coord != null; coord = coord.getNextSibling())
                                        if ("teleport".equalsIgnoreCase(coord.getNodeName())) {
                                            attrs = coord.getAttributes();
                                            tele = Location.of(attrs.getNamedItem("loc").getNodeValue());
                                        } else if ("zone".equalsIgnoreCase(coord.getNodeName())) {
                                            attrs = coord.getAttributes();
                                            xMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
                                            xMax = Integer.parseInt(attrs.getNamedItem("xMax").getNodeValue());
                                            yMin = Integer.parseInt(attrs.getNamedItem("yMin").getNodeValue());
                                            yMax = Integer.parseInt(attrs.getNamedItem("yMax").getNodeValue());
                                            zMin = Integer.parseInt(attrs.getNamedItem("zMin").getNodeValue());
                                            zMax = Integer.parseInt(attrs.getNamedItem("zMax").getNodeValue());

                                            territory = new Territory().add(new Rectangle(xMin, yMin, xMax, yMax).setZmin(zMin).setZmax(zMax));
                                        }

                                    if (territory == null)
                                        LOG.error("DimensionalRiftManager: invalid spawn data for room id " + roomId + "!");

                                    if (!rooms.containsKey(type))
                                        rooms.put(type, new ConcurrentHashMap<>());

                                    rooms.get(type).put(roomId, new DimensionalRiftRoom(territory, tele, isBossRoom));

                                    for (Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
                                        if ("spawn".equalsIgnoreCase(spawn.getNodeName())) {
                                            attrs = spawn.getAttributes();
                                            mobId = Integer.parseInt(attrs.getNamedItem("mobId").getNodeValue());
                                            delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
                                            count = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());

                                            if (!rooms.containsKey(type))
                                                LOG.warn("Type " + type + " not found!");
                                            else if (!rooms.get(type).containsKey(roomId))
                                                LOG.warn("Room " + roomId + " in Type " + type + " not found!");

                                            if (rooms.containsKey(type) && rooms.get(type).containsKey(roomId)) {
                                                spawnDat = new SimpleSpawner(mobId);
                                                spawnDat.setTerritory(territory)
                                                        .setRespawnDelay(delay)
                                                        .setAmount(count);
                                                rooms.get(type).get(roomId).getSpawns().add(spawnDat);
                                                countGood++;
                                            } else
                                                countBad++;
                                        }
                                }
                        }
        } catch (NumberFormatException e) {
            LOG.error("DimensionalRiftManager: File not Found!", e);
        } catch (DOMException | IllegalArgumentException | ParserConfigurationException | SAXException e) {
            LOG.error("DimensionalRiftManager: Error on loading DimensionalRift spawns!", e);
        } catch (IOException e) {
            LOG.error("DimensionalRiftManager: IOException on loading DimensionalRift spawns!", e);
        }
        int typeSize = rooms.keySet().size();
        int roomSize = 0;

        for (int b : rooms.keySet())
            roomSize += rooms.get(b).keySet().size();

        LOG.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
        LOG.info("DimensionalRiftManager: Loaded " + countGood + " DimensionalRift spawns, " + countBad + " errors.");
    }


    public boolean checkIfInRiftZone(Location loc, boolean ignorePeaceZone) {
        if (ignorePeaceZone)
            return rooms.get(0).get(1).checkIfInZone(loc);
        return rooms.get(0).get(1).checkIfInZone(loc) && !rooms.get(0).get(0).checkIfInZone(loc);
    }

    private boolean checkIfInPeaceZone(Location loc) {
        return rooms.get(0).get(0).checkIfInZone(loc);
    }

    public void teleportToWaitingRoom(Player player) {
        teleToLocation(player, Location.findPointToStay(getRoom(0, 0).getTeleportCoords(), 0, 250, ReflectionManager.DEFAULT.getGeoIndex()), null);
    }

    public void start(Player player, int type, NpcInstance npc) {
        if (!player.isInParty()) {
            showHtmlFile(player, "rift/NoParty.htm", npc);
            return;
        }

        if (player.isGM())
            return;

        if (!player.getParty().isLeader(player)) {
            showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        if (player.getParty().isInDimensionalRift()) {
            showHtmlFile(player, "rift/Cheater.htm", npc);

            if (!player.isGM())
                LOG.warn("Player " + player.getName() + "(" + player.objectId() + ") was cheating in dimension rift area!");

            return;
        }

        if (player.getParty().size() < Config.RIFT_MIN_PARTY_SIZE) {
            showHtmlFile(player, "rift/SmallParty.htm", npc);
            return;
        }

        if (player.getParty().getMembersStream()
                .filter(p -> !checkIfInPeaceZone(p.getLoc()))
                .peek(p -> showHtmlFile(player, "rift/NotInWaitingRoom.htm", npc))
                .findAny().isPresent())
            return;


//            ItemInstance i;
        if (player.getParty().getMembersStream()
                .map(p -> p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID))
                .filter(i -> i == null || i.getCount() < getNeededItems(type))
                .peek(i -> showHtmlFile(player, "rift/NoFragments.htm", npc))
                .findAny().isPresent())
            return;


        if (player.getParty().getMembersStream()
                .filter(p -> !p.getInventory().destroyItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID, getNeededItems(type), "DimensionalRift"))
                .peek(p -> showHtmlFile(player, "rift/NoFragments.htm", npc))
                .findAny().isPresent())
            return;


        new DimensionalRift(player.getParty(), type, Rnd.get(1, rooms.get(type).size() - 1));
    }

    private long getNeededItems(int type) {
        return 3 * type + 15;
    }

    public void showHtmlFile(Player player, String file, NpcInstance npc) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
        html.setFile(file);
        html.replace("%t_name%", npc.getName());
        player.sendPacket(html);
    }

    public class DimensionalRiftRoom {
        private final Territory territory;
        private final Location teleportCoords;
        private final boolean isBossRoom;
        private final List<SimpleSpawner> roomSpawns;

        DimensionalRiftRoom(Territory territory, Location tele, boolean isBossRoom) {
            this.territory = territory;
            teleportCoords = tele;
            this.isBossRoom = isBossRoom;
            roomSpawns = new ArrayList<>();
        }

        public Location getTeleportCoords() {
            return teleportCoords;
        }

        public boolean checkIfInZone(Location loc) {
            return territory.isInside(loc);
        }

        public boolean isBossRoom() {
            return isBossRoom;
        }

        public List<SimpleSpawner> getSpawns() {
            return roomSpawns;
        }
    }
}