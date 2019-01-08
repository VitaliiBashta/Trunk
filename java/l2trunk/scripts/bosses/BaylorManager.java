package l2trunk.scripts.bosses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.FlyToLocation;
import l2trunk.gameserver.network.serverpackets.FlyToLocation.FlyType;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class BaylorManager extends Functions implements ScriptFile {
    private static final int Baylor = 29099;
    private static final int CrystalPrisonGuard = 29100;
    private static final int Parme = 32271;
    private static final int Oracle = 32273;
    private static final List<Location> _crystalineLocation = List.of(
            new Location(154404, 140596, -12711, 44732),
            new Location(153574, 140402, -12711, 44732),
            new Location(152105, 141230, -12711, 44732),
            new Location(151877, 142095, -12711, 44732),
            new Location(152109, 142920, -12711, 44732),
            new Location(152730, 143555, -12711, 44732),
            new Location(154439, 143538, -12711, 44732),
            new Location(155246, 142068, -12711, 44732));
    private static final List<Location> _baylorChestLocation = List.of(
            new Location(153763, 142075, -12741, 64792),
            new Location(153701, 141942, -12741, 57739),
            new Location(153573, 141894, -12741, 49471),
            new Location(153445, 141945, -12741, 41113),
            new Location(153381, 142076, -12741, 32767),
            new Location(153441, 142211, -12741, 25730),
            new Location(153573, 142260, -12741, 16185),
            new Location(153706, 142212, -12741, 7579),
            new Location(153571, 142860, -12741, 16716),
            new Location(152783, 142077, -12741, 32176),
            new Location(153571, 141274, -12741, 49072),
            new Location(154365, 142073, -12741, 64149),
            new Location(154192, 142697, -12741, 7894),
            new Location(152924, 142677, -12741, 25072),
            new Location(152907, 141428, -12741, 39590),
            new Location(154243, 141411, -12741, 55500));
    private static final List<Integer> doors = List.of(
            24220009, 24220011, 24220012, 24220014, 24220015, 24220016, 24220017, 24220019);
    // Instance of monsters
    private static final List<NpcInstance> CRYSTALINE = new ArrayList<>();
    private static final int FWBA_ACTIVITYTIMEOFMOBS = 120 * 60000;
    private static final int FWBA_FIXINTERVALOFBAYLORSPAWN = Config.FIXINTERVALOFBAYLORSPAWN_HOUR * 60 * 60000;
    private static final int FWBA_RANDOMINTERVALOFBAYLORSPAWN = Config.RANDOMINTERVALOFBAYLORSPAWN * 60 * 60000;
    private static final boolean FWBA_ENABLESINGLEPLAYER = false;
    private static NpcInstance _baylor;
    // Tasks
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _activityTimeEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _endSceneTask = null;
    // State of baylor's lair.
    private static boolean _isAlreadyEnteredOtherParty = false;
    private static EpicBossState _state;
    private static Zone zone;
    private static boolean Dying = false;
    private static int currentReflection;

    public static NpcInstance spawn(Location loc, int npcId) {
        NpcTemplate template = NpcHolder.getTemplate(npcId);
        return (NpcInstance) template.getNewInstance()
                .setSpawnedLoc(loc)
                .setHeading(loc.h)
                .setLoc(loc)
                .setReflection(currentReflection)
                .spawnMe();
    }

    // Whether it is permitted to enter the baylor's lair is confirmed.
    public static int canIntoBaylorLair(Player pc) {
        if (pc.isGM())
            return 0;
        if (!FWBA_ENABLESINGLEPLAYER && !pc.isInParty())
            return 4;
        else if (_isAlreadyEnteredOtherParty)
            return 2;
        else if (_state.getState().equals(EpicBossState.State.NOTSPAWN))
            return 0;
        else if (_state.getState().equals(EpicBossState.State.ALIVE) || _state.getState().equals(EpicBossState.State.DEAD))
            return 1;
        else if (_state.getState().equals(EpicBossState.State.INTERVAL))
            return 3;
        else
            return 0;
    }

    private synchronized static void checkAnnihilated() {
        if (isPlayersAnnihilated())
            setIntervalEndTask();
    }

    // Teleporting player to baylor's lair.
    public synchronized static void entryToBaylorLair(Player pc) {
        currentReflection = pc.getReflectionId();
        zone.setReflection(pc.getReflection());

        // Synerge - When they enter baylor cave, reset the instance time to 30 minutes
        if (pc.getReflection() != null)
            pc.getReflection().startCollapseTimer(30 * 60 * 1000L);

        ReflectionManager.INSTANCE.get(currentReflection).closeDoor(24220008);
        ThreadPoolManager.INSTANCE.schedule(new BaylorSpawn(CrystalPrisonGuard), 20000);
        ThreadPoolManager.INSTANCE.schedule(new BaylorSpawn(Baylor), 40000);

        if (pc.getParty() == null) {
            pc.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
            pc.setBlock(true);
        } else {

            pc.getParty().getMembers().stream()
                    .filter(mem -> !mem.isDead())
                    .filter(mem -> mem.isInRange(pc, 1500))
                    .forEach(mem -> {
                        mem.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
                        mem.setBlock(true);
                    });
        }
        _isAlreadyEnteredOtherParty = true;
    }

    private static List<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static int getRespawnInterval() {
        return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWBA_FIXINTERVALOFBAYLORSPAWN + Rnd.get(0, FWBA_RANDOMINTERVALOFBAYLORSPAWN)));
    }

    private static Zone getZone() {
        return zone;
    }

    private static void init() {
        _state = new EpicBossState(Baylor);
        zone = ReflectionUtils.getZone("[baylor_epic]");
        zone.addListener(new BaylorZoneListener());

        _isAlreadyEnteredOtherParty = false;

        Log.add("BaylorManager : State of Baylor is " + _state.getState() + ".", "bosses");
        if (!_state.getState().equals(EpicBossState.State.NOTSPAWN))
            setIntervalEndTask();

        Date dt = new Date(_state.getRespawnDate());
        Log.add("BaylorManager : Next spawn date of Baylor is " + dt + ".", "bosses");
        Log.add("BaylorManager : Init BaylorManager.", "bosses");
    }

    private static boolean isPlayersAnnihilated() {
        for (Player pc : getPlayersInside())
            if (!pc.isDead())
                return false;
        return true;
    }

    private static void onBaylorDie() {
        if (Dying)
            return;

        Dying = true;
        _state.setRespawnDate(getRespawnInterval());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();

        Log.add("Baylor died", "bosses");

        spawn(Rnd.get(_baylorChestLocation), 29116);

        spawn(new Location(153570, 142067, -9727, 55500), Parme);
        spawn(new Location(153569, 142075, -12732, 55500), Oracle);

        startCollapse();
    }

    // Task of interval of baylor spawn.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState().equals(EpicBossState.State.ALIVE)) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }

        if (!_state.getState().equals(EpicBossState.State.INTERVAL)) {
            _state.setRespawnDate(getRespawnInterval());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }, _state.getInterval());
    }

    // Clean up Baylor's lair.
    private static void setUnspawn() {
        if (!_isAlreadyEnteredOtherParty)
            return;
        _isAlreadyEnteredOtherParty = false;

        startCollapse();

        if (_baylor != null)
            _baylor.deleteMe();
        _baylor = null;

        for (NpcInstance npc : CRYSTALINE)
            if (npc != null)
                npc.deleteMe();

        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_activityTimeEndTask != null) {
            _activityTimeEndTask.cancel(false);
            _activityTimeEndTask = null;
        }
    }

    private static void startCollapse() {
        if (currentReflection > 0) {
            Reflection reflection = ReflectionManager.INSTANCE.get(currentReflection);
            if (reflection != null)
                reflection.startCollapseTimer(300000);
            currentReflection = 0;
        }
    }

    @Override
    public void onLoad() {
        init();
    }

    @Override
    public void onReload() {
        setUnspawn();
    }

    @Override
    public void onShutdown() {
    }

    private static class BaylorSpawn extends RunnableImpl {
        private final int _npcId;
        private final Location _pos = new Location(153569, 142075, -12711, 44732);

        BaylorSpawn(int npcId) {
            _npcId = npcId;
        }

        @Override
        public void runImpl() {
            switch (_npcId) {
                case CrystalPrisonGuard:

                    Reflection ref = ReflectionManager.INSTANCE.get(currentReflection);
                    doors.forEach(ref::openDoor);

                    for (int i = 0; i < _crystalineLocation.size(); i++) {
                        CRYSTALINE.add(spawn(_crystalineLocation.get(i), CrystalPrisonGuard));
                        CRYSTALINE.get(i).setRunning();
                        CRYSTALINE.get(i).moveToLocation(_pos, 300, false);
                        ThreadPoolManager.INSTANCE.schedule(new Social(CRYSTALINE.get(i), 2), 15000);
                    }

                    break;
                case Baylor:
                    Dying = false;

                    _baylor = spawn(new Location(153569, 142075, -12732, 59864), Baylor);
                    _baylor.addListener(BaylorDeathListener.getInstance());

                    _state.setRespawnDate(getRespawnInterval() + FWBA_ACTIVITYTIMEOFMOBS);
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();

                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(_baylor, 1), 500);

                    if (_endSceneTask != null) {
                        _endSceneTask.cancel(false);
                        _endSceneTask = null;
                    }
                    _endSceneTask = ThreadPoolManager.INSTANCE.schedule(new EndScene(), 23000);

                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.INSTANCE.schedule(BaylorManager::setIntervalEndTask, FWBA_ACTIVITYTIMEOFMOBS);

                    break;
            }
        }
    }

    private static class Social extends RunnableImpl {
        private final int action;
        private final NpcInstance npc;

        Social(NpcInstance npc, int actionId) {
            this.npc = npc;
            action = actionId;
        }

        @Override
        public void runImpl() {
            npc.broadcastPacket(new SocialAction(npc.getObjectId(), action));
        }
    }

    private static class EndScene extends RunnableImpl {
        @Override
        public void runImpl() {
            for (Player player : getPlayersInside()) {
                player.setBlock();
                if (_baylor != null) {
                    double angle = PositionUtils.convertHeadingToDegree(_baylor.getHeading());
                    double radian = Math.toRadians(angle - 90);
                    int x1 = -(int) (Math.sin(radian) * 600);
                    int y1 = (int) (Math.cos(radian) * 600);
                    Location flyLoc = GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), player.getX() + x1, player.getY() + y1, player.getGeoIndex());
                    player.setLoc(flyLoc);
                    player.broadcastPacket(new FlyToLocation(player, flyLoc, FlyType.THROW_HORIZONTAL));
                }
            }
            CRYSTALINE.forEach(npc ->
                    npc.reduceCurrentHp(npc.getMaxHp() + 1, npc, null, true, true, false, false, false, false, false));
        }
    }

    private static class BaylorZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature actor) {
            if (actor.isPlayer())
                actor.addListener(PlayerDeathListener.getInstance());
        }

        @Override
        public void onZoneLeave(Zone zone, Creature actor) {
            if (actor.isPlayer())
                actor.removeListener(PlayerDeathListener.getInstance());
        }
    }

    private static class PlayerDeathListener implements OnDeathListener {
        private static final OnDeathListener _instance = new PlayerDeathListener();

        static OnDeathListener getInstance() {
            return _instance;
        }

        @Override
        public void onDeath(Creature actor, Creature killer) {
            checkAnnihilated();
        }
    }

    private static class BaylorDeathListener implements OnDeathListener {
        private static final OnDeathListener _instance = new BaylorDeathListener();

        static OnDeathListener getInstance() {
            return _instance;
        }

        @Override
        public void onDeath(Creature actor, Creature killer) {
            onBaylorDie();
        }
    }
}