package l2trunk.gameserver.instancemanager;

import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Manages 11 stages of Hellbound Island and all it's events
 */

public enum HellboundManager {
    INSTANCE;
    private static final long _taskDelay = 2 * 60 * 1000L; //30min
    private static List<HellboundSpawn> _list = new ArrayList<>();
    private static List<SimpleSpawner> _spawnList = new ArrayList<>();
    private static int _initialStage = getHellboundLevel();
    private final Logger LOG = LoggerFactory.getLogger(HellboundManager.class);
    private final DeathListener _deathListener = new DeathListener();

    public static long getConfidence() {
        return ServerVariables.getLong("HellboundConfidence");
    }

    public static void setConfidence(long value) {
        ServerVariables.set("HellboundConfidence", value);
    }

    public static void addConfidence(long value) {
        ServerVariables.set("HellboundConfidence", Math.round(getConfidence() + value * Config.RATE_HELLBOUND_CONFIDENCE));
    }

    public static void reduceConfidence(long value) {
        long i = getConfidence() - value;
        ServerVariables.set("HellboundConfidence", i < 1 ? 1 : i);
    }

    public static int getHellboundLevel() {
        if (Config.HELLBOUND_LEVEL <= getHellboundLevelS()) {
            return getHellboundLevelS();
        }
        return Config.HELLBOUND_LEVEL;
    }

    private static int getHellboundLevelS() {
        long confidence = ServerVariables.getLong("HellboundConfidence");
        boolean judesBoxes = ServerVariables.getBool("HB_judesBoxes", false);
        boolean bernardBoxes = ServerVariables.getBool("HB_bernardBoxes", false);
        boolean derekKilled = ServerVariables.getBool("HB_derekKilled", false);
        boolean captainKilled = ServerVariables.getBool("HB_captainKilled", false);

        if (confidence < 1)
            return 0;
        else if (confidence < 300000)
            return 1;
        else if (confidence < 600000)
            return 2;
        else if (confidence < 1000000)
            return 3;
        else if (confidence < 1200000) {
            if (derekKilled && judesBoxes && bernardBoxes)
                return 5;
            else if (!derekKilled && judesBoxes && bernardBoxes)
                return 4;
            else if (!derekKilled)
                return 3;
        } else if (confidence < 1500000)
            return 6;
        else if (confidence < 1800000)
            return 7;
        else if (confidence < 2100000) {
            if (captainKilled)
                return 9;
            else
                return 8;
        } else if (confidence < 2200000)
            return 10;
        else return 11;

        return 0;
    }

    private static void doorHandler() {
        final int NativeHell_native0131 = 19250001; // Kief room
        final int NativeHell_native0132 = 19250002;
        final int NativeHell_native0133 = 19250003; // Another room
        final int NativeHell_native0134 = 19250004;

        final int sdoor_trans_mesh00 = 20250002;
        final int Hell_gate_door = 20250001;

        final List<Integer> doors = List.of(
                NativeHell_native0131,
                NativeHell_native0132,
                NativeHell_native0133,
                NativeHell_native0134,
                sdoor_trans_mesh00,
                Hell_gate_door);

        doors.forEach(d -> ReflectionUtils.getDoor(d).closeMe());

        switch (getHellboundLevel()) {
            case 5:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                break;
            case 6:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                break;
            case 7:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
                break;
            case 8:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
                break;
            case 9:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
                ReflectionUtils.getDoor(Hell_gate_door).openMe();
                break;
            case 10:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
                ReflectionUtils.getDoor(Hell_gate_door).openMe();
                break;
            case 11:
                ReflectionUtils.getDoor(NativeHell_native0131).openMe();
                ReflectionUtils.getDoor(NativeHell_native0132).openMe();
                ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
                ReflectionUtils.getDoor(Hell_gate_door).openMe();
                break;
            default:
                break;
        }
    }

    public void init() {
        getHellboundSpawn();
        spawnHellbound();
        doorHandler();
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new StageCheckTask(), _taskDelay, _taskDelay);

    }

    private void spawnHellbound() {
        SimpleSpawner spawnDat;

        for (HellboundSpawn hbsi : _list)
            if (hbsi.stages.contains(getHellboundLevel())) {
                for (int i = 0; i < hbsi._amount; i++) {
                    spawnDat = new SimpleSpawner(hbsi.getNpcId());
                    spawnDat.setAmount(1);
                    if (hbsi.getLoc() != null)
                        spawnDat.setLoc(hbsi.getLoc());
                    if (hbsi.getSpawnTerritory() != null)
                        spawnDat.setTerritory(hbsi.getSpawnTerritory());
                    spawnDat.setReflection(ReflectionManager.DEFAULT);
                    spawnDat.setRespawnDelay(hbsi.getRespawn(), hbsi.getRespawnRnd());
                    spawnDat.setRespawnTime(0);
                    spawnDat.doSpawn(true);
                    spawnDat.getLastSpawn().addListener(_deathListener);
                    spawnDat.startRespawn();
                    _spawnList.add(spawnDat);
                }
            }
        LOG.info("HellboundManager: Spawned " + _spawnList.size() + " mobs and NPCs according to the current Hellbound stage");
    }

    private void getHellboundSpawn() {
        try {
            Path file = Config.DATAPACK_ROOT.resolve("/data/hellbound_spawnlist.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc1 = factory.newDocumentBuilder().parse(file.toFile());

            int counter = 0;
            for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
                if ("list".equalsIgnoreCase(n1.getNodeName()))
                    for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
                        if ("data".equalsIgnoreCase(d1.getNodeName())) {
                            counter++;
                            int npcId = Integer.parseInt(d1.getAttributes().getNamedItem("npc_id").getNodeValue());
                            Location spawnLoc = null;
                            if (d1.getAttributes().getNamedItem("loc") != null)
                                spawnLoc = Location.of(d1.getAttributes().getNamedItem("loc").getNodeValue());
                            int count = 1;
                            if (d1.getAttributes().getNamedItem("count") != null)
                                count = Integer.parseInt(d1.getAttributes().getNamedItem("count").getNodeValue());
                            int respawn = 60;
                            if (d1.getAttributes().getNamedItem("respawn") != null)
                                respawn = Integer.parseInt(d1.getAttributes().getNamedItem("respawn").getNodeValue());
                            int respawnRnd = 0;
                            if (d1.getAttributes().getNamedItem("respawn_rnd") != null)
                                respawnRnd = Integer.parseInt(d1.getAttributes().getNamedItem("respawn_rnd").getNodeValue());

                            Node att = d1.getAttributes().getNamedItem("stage");
                            StringTokenizer st = new StringTokenizer(att.getNodeValue(), ";");
                            int tokenCount = st.countTokens();
                            List<Integer> stages = new ArrayList<>(tokenCount);
                            for (int i = 0; i < tokenCount; i++) {
                                Integer value = Integer.decode(st.nextToken().trim());
                                stages.add(value);
                            }

                            Territory territory = null;
                            for (Node s1 = d1.getFirstChild(); s1 != null; s1 = s1.getNextSibling())
                                if ("territory".equalsIgnoreCase(s1.getNodeName())) {

                                    Polygon poly = new Polygon();
                                    for (Node s2 = s1.getFirstChild(); s2 != null; s2 = s2.getNextSibling())
                                        if ("add".equalsIgnoreCase(s2.getNodeName())) {
                                            int x = Integer.parseInt(s2.getAttributes().getNamedItem("x").getNodeValue());
                                            int y = Integer.parseInt(s2.getAttributes().getNamedItem("y").getNodeValue());
                                            int minZ = Integer.parseInt(s2.getAttributes().getNamedItem("zmin").getNodeValue());
                                            int maxZ = Integer.parseInt(s2.getAttributes().getNamedItem("zmax").getNodeValue());
                                            poly.add(x, y).setZmin(minZ).setZmax(maxZ);
                                        }

                                    territory = new Territory().add(poly);

                                    if (!poly.validate()) {
                                        LOG.error("HellboundManager: Invalid spawn territory : " + poly + '!');
                                    }
                                }

                            if (spawnLoc == null && territory == null) {
                                LOG.error("HellboundManager: no spawn data for npc id : " + npcId + '!');
                                continue;
                            }

                            HellboundSpawn hbs = new HellboundSpawn(npcId, spawnLoc, count, territory, respawn, respawnRnd, stages);
                            _list.add(hbs);
                        }

            LOG.info("HellboundManager: Loaded " + counter + " spawn entries.");
        } catch (NumberFormatException | DOMException | ParserConfigurationException | SAXException e) {
            LOG.info("HellboundManager: Spawn table could not be initialized." + e);
        } catch (IOException | IllegalArgumentException e) {
            LOG.info("HellboundManager: IOException or IllegalArgumentException." + e);
        }
    }

    private void despawnHellbound() {
        for (SimpleSpawner spawnToDelete : _spawnList)
            spawnToDelete.deleteAll();

        _spawnList.clear();
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature cha, Creature killer) {
            if (!(cha instanceof MonsterInstance) || !(killer instanceof Playable))
                return;
            int npcId = cha.getNpcId();
            switch (getHellboundLevel()) {
                case 0:
                    break;
                case 1: {
                    switch (npcId) {
                        case 22320: // Junior Watchman
                        case 22321: // Junior Summoner
                        case 22324: // Blind Huntsman
                        case 22325: // Blind Watchman
                            addConfidence(1);
                            break;
                        case 22327: // Arcane Scout
                        case 22328: // Arcane Guardian
                        case 22329: // Arcane Watchman
                            addConfidence(3); // confirmed
                            break;
                        case 22322: // Subjugated Native
                        case 22323: // Charmed Native
                        case 32299: // Quarry Slave
                            reduceConfidence(10);
                            break;
                    }
                    break;
                }
                case 2: {
                    switch (npcId) {
                        case 18463: // Remnant Diabolist
                        case 18464: // Remnant Diviner
                            addConfidence(5);
                            break;
                        case 22322: // Subjugated Native
                        case 22323: // Charmed Native
                        case 32299: // Quarry Slave
                            reduceConfidence(10);
                            break;
                    }
                    break;
                }
                case 3: {
                    switch (npcId) {
                        case 22342: // Darion's Enforcer
                        case 22343: // Darion's Executioner
                            addConfidence(3);
                            break;
                        case 22341: // Keltas
                            addConfidence(100);
                            break;
                        case 22322: // Subjugated Native
                        case 22323: // Charmed Native
                        case 32299: // Quarry Slave
                            reduceConfidence(10);
                            break;
                    }
                    break;
                }
                case 4: {
                    switch (npcId) {
                        case 18465: // Derek
                            addConfidence(10000);
                            ServerVariables.set("HB_derekKilled", true);
                            break;
                        case 22322: // Subjugated Native
                        case 22323: // Charmed Native
                        case 32299: // Quarry Slave
                            reduceConfidence(10);
                            break;
                    }
                    break;
                }
                case 5: {
                    // Leodas
                    if (npcId == 22448) {
                        reduceConfidence(50);
                    }
                    break;
                }
                case 6: {
                    switch (npcId) {
                        case 22326: // Hellinark
                            addConfidence(500);
                            break;
                        case 18484: // Naia Failan
                            addConfidence(5);
                            break;
                    }
                    break;
                }
                case 8: {
                    // Outpost Captain
                    if (npcId == 18466) {
                        addConfidence(10000);
                        ServerVariables.set("HB_captainKilled", true);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private class StageCheckTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_initialStage != getHellboundLevel()) {
                despawnHellbound();
                spawnHellbound();
                doorHandler();
                _initialStage = getHellboundLevel();
            }
        }
    }

    class HellboundSpawn {
        private final int _npcId;
        private final Location _loc;
        private final int _amount;
        private final Territory _spawnTerritory;
        private final int _respawn;
        private final int _respawnRnd;
        private final List<Integer> stages;

        HellboundSpawn(int npcId, Location loc, int amount, Territory spawnTerritory, int respawn, int respawnRnd, List<Integer> stages) {
            _npcId = npcId;
            _loc = loc;
            _amount = amount;
            _spawnTerritory = spawnTerritory;
            _respawn = respawn;
            _respawnRnd = respawnRnd;
            this.stages = stages;
        }

        int getNpcId() {
            return _npcId;
        }

        Location getLoc() {
            return _loc;
        }


        Territory getSpawnTerritory() {
            return _spawnTerritory;
        }

        int getRespawn() {
            return _respawn;
        }

        int getRespawnRnd() {
            return _respawnRnd;
        }


    }
}