package l2trunk.scripts.bosses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.BossInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.Earthquake;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.TimeUtils;
import l2trunk.scripts.bosses.EpicBossState.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public final class BaiumManager extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger LOG = LoggerFactory.getLogger(BaiumManager.class);
    private static final List<NpcInstance> _monsters = new ArrayList<>();
    private static final Map<Integer, SimpleSpawner> _monsterSpawn = new ConcurrentHashMap<>();
    private static final List<NpcInstance> _angels = new ArrayList<>();
    private static final List<SimpleSpawner> _angelSpawns = new ArrayList<>();
    private final static int ARCHANGEL = 29021;
    private final static int BAIUM = 29020;
    private final static int BAIUM_NPC = 29025;
    // location of arcangels.
    private final static Location[] ANGEL_LOCATION = new Location[]
            {
                    new Location(113004, 16209, 10076, 60242),
                    new Location(114053, 16642, 10076, 4411),
                    new Location(114563, 17184, 10076, 49241),
                    new Location(116356, 16402, 10076, 31109),
                    new Location(115015, 16393, 10076, 32760),
                    new Location(115481, 15335, 10076, 16241),
                    new Location(114680, 15407, 10051, 32485),
                    new Location(114886, 14437, 10076, 16868),
                    new Location(115391, 17593, 10076, 55346),
                    new Location(115245, 17558, 10076, 35536)
            };
    // location of teleport cube.
    private final static Location CUBE_LOCATION = new Location(115203, 16620, 10078, 0);
    private final static Location STATUE_LOCATION = new Location(115996, 17417, 10106, 41740);
    private final static int TELEPORT_CUBE = 31759;
    private final static int FWB_LIMITUNTILSLEEP = 30 * 60000;
    private final static int FWB_FIXINTERVALOFBAIUM = Config.BAIUM_DEFAULT_SPAWN_HOURS * 60 * 60000;
    private final static int FWB_RANDOMINTERVALOFBAIUM = Config.BAIUM_RANDOM_SPAWN_HOURS * 60 * 60000;
    public static SimpleSpawner _statueSpawn = null;
    // tasks.
    private static ScheduledFuture<?> _callAngelTask = null;
    private static ScheduledFuture<?> _cubeSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _killPcTask = null;
    private static ScheduledFuture<?> _mobiliseTask = null;
    private static ScheduledFuture<?> _moveAtRandomTask = null;
    private static ScheduledFuture<?> _sleepCheckTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _socialTask2 = null;
    private static ScheduledFuture<?> _onAnnihilatedTask = null;
    private static EpicBossState state;
    private static long _lastAttackTime = 0;
    private static NpcInstance _npcBaium;
    private static NpcInstance _teleportCube = null;
    private static SimpleSpawner _teleportCubeSpawn = null;
    private static Zone zone;
    private static boolean Dying = false;

    private static void banishForeigners() {
        for (Player player : getPlayersInside())
            player.teleToClosestTown();
    }

    private synchronized static void checkAnnihilated() {
        if (_onAnnihilatedTask == null && isPlayersAnnihilated())
            _onAnnihilatedTask = ThreadPoolManager.INSTANCE.schedule(BaiumManager::sleepBaium, 5000);
    }

    // Archangel ascension.
    private static void deleteArchangels() {
        _angels.stream()
                .filter(angel -> angel.getSpawn() != null)
                .forEach(angel -> {
                    angel.getSpawn().stopRespawn();
                    angel.deleteMe();
                });
        _angels.clear();
    }

    private static List<Player> getPlayersInside() {
        return zone.getInsidePlayers();
    }

    public static Zone getZone() {
        return zone;
    }

    private static boolean isPlayersAnnihilated() {
        return getPlayersInside().stream().allMatch(Player::isDead);
    }

    private static void onBaiumDie(Creature creature) {
        if (Dying)
            return;

        Dying = true;
        creature.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, creature.getLoc()));
        state.setRespawnDate(getRespawnInterval());
        state.setState(State.INTERVAL);
        state.update();

        Log.add("Baium died", "bosses");

        deleteArchangels();

        _cubeSpawnTask = ThreadPoolManager.INSTANCE.schedule(() -> _teleportCube = _teleportCubeSpawn.doSpawn(true), 10000L);
    }

    private static long getRespawnInterval() {
        return (long) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWB_FIXINTERVALOFBAIUM + Rnd.get(0, FWB_RANDOMINTERVALOFBAIUM)));
    }

    // start interval.
    private static void setIntervalEndTask() {
        setUnspawn();

        //init state of Baium's lair.
        if (!state.getState().equals(State.INTERVAL)) {
            state.setRespawnDate(getRespawnInterval());
            state.setState(State.INTERVAL);
            state.update();
        }

        _intervalEndTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            state.setState(State.NOTSPAWN);
            state.update();

            // statue of Baium respawn.
            _statueSpawn.doSpawn(true);
        }, state.getInterval());
    }

    public static void setLastAttackTime() {
        _lastAttackTime = System.currentTimeMillis();
    }

    // clean Baium's lair.
    private static void setUnspawn() {
        // eliminate players.
        banishForeigners();

        // delete monsters.
        deleteArchangels();
        for (NpcInstance mob : _monsters) {
            mob.getSpawn().stopRespawn();
            mob.deleteMe();
        }
        _monsters.clear();

        // delete teleport cube.
        if (_teleportCube != null) {
            _teleportCube.getSpawn().stopRespawn();
            _teleportCube.deleteMe();
            _teleportCube = null;
        }

        // not executed tasks is canceled.
        if (_cubeSpawnTask != null) {
            _cubeSpawnTask.cancel(false);
            _cubeSpawnTask = null;
        }
        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_socialTask != null) {
            _socialTask.cancel(false);
            _socialTask = null;
        }
        if (_mobiliseTask != null) {
            _mobiliseTask.cancel(false);
            _mobiliseTask = null;
        }
        if (_moveAtRandomTask != null) {
            _moveAtRandomTask.cancel(false);
            _moveAtRandomTask = null;
        }
        if (_socialTask2 != null) {
            _socialTask2.cancel(false);
            _socialTask2 = null;
        }
        if (_killPcTask != null) {
            _killPcTask.cancel(false);
            _killPcTask = null;
        }
        if (_callAngelTask != null) {
            _callAngelTask.cancel(false);
            _callAngelTask = null;
        }
        if (_sleepCheckTask != null) {
            _sleepCheckTask.cancel(false);
            _sleepCheckTask = null;
        }
        if (_onAnnihilatedTask != null) {
            _onAnnihilatedTask.cancel(false);
            _onAnnihilatedTask = null;
        }
    }

    // Baium sleeps if not attacked for 30 minutes.
    private static void sleepBaium() {
        setUnspawn();
        Log.add("Baium going to sleep, spawning statue", "bosses");
        state.setState(State.NOTSPAWN);
        state.update();

        // statue of Baium respawn.
        _statueSpawn.doSpawn(true);
    }

    // do spawn Baium.
    public static void spawnBaium(NpcInstance npcBaium, Player awakeBy) {
        Dying = false;
        _npcBaium = npcBaium;

        // do spawn.
        SimpleSpawner baiumSpawn = _monsterSpawn.get(BAIUM);
        baiumSpawn.setLoc(_npcBaium.getLoc());

        // delete statue
        _npcBaium.getSpawn().stopRespawn();
        _npcBaium.deleteMe();

        final BossInstance baium = (BossInstance) baiumSpawn.doSpawn(true);
        _monsters.add(baium);

        state.setRespawnDate(getRespawnInterval());
        state.setState(State.ALIVE);
        state.update();

        Log.add("Spawned Baium, awake by: " + awakeBy, "bosses");

        // set last attack time.
        setLastAttackTime();

        baium.startImmobilized();
        baium.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, 0, baium.getLoc()));
        baium.broadcastPacket(new SocialAction(baium.getObjectId(), 2));

        _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(baium, 3), 15000);

        ThreadPoolManager.INSTANCE.schedule(new EarthquakeTask(baium), 25000);

        _socialTask2 = ThreadPoolManager.INSTANCE.schedule(new Social(baium, 1), 25000);
        _killPcTask = ThreadPoolManager.INSTANCE.schedule(new KillPc(awakeBy, baium), 26000);
        _callAngelTask = ThreadPoolManager.INSTANCE.schedule(new CallArchAngel(), 35000);
        _mobiliseTask = ThreadPoolManager.INSTANCE.schedule(new SetMobilised(baium), 35500);

        // move at random.
        Location pos = new Location(Rnd.get(112826, 116241), Rnd.get(15575, 16375), 10078, 0);
        _moveAtRandomTask = ThreadPoolManager.INSTANCE.schedule(new MoveAtRandom(baium, pos), 36000);

        _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), 600000);
    }

    public static EpicBossState getState() {
        return state;
    }

    private void init() {
        state = new EpicBossState(BAIUM);
        zone = ReflectionUtils.getZone("[baium_epic]");

        CharListenerList.addGlobal(this);
        try {
            SimpleSpawner tempSpawn;

            // Statue of Baium
            _statueSpawn = new SimpleSpawner(BAIUM_NPC);
            _statueSpawn.setLoc(STATUE_LOCATION)
                    .setAmount(1)
                    .stopRespawn();

            // Baium
            tempSpawn = new SimpleSpawner(BAIUM);
            tempSpawn.setAmount(1);
            _monsterSpawn.put(BAIUM, tempSpawn);
        } catch (RuntimeException e) {
            LOG.error("Error on Baium Init", e);
        }

        // teleport Cube
        _teleportCubeSpawn = (SimpleSpawner) new SimpleSpawner(TELEPORT_CUBE)
                .setLoc(CUBE_LOCATION)
                .setAmount(1)
                .setRespawnDelay(60);

        // Archangels
        _angelSpawns.clear();

        // 5 random numbers of 10, no duplicates
        List<Integer> random = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int r = -1;
            while (r == -1 || random.contains(r))
                r = Rnd.get(10);
            random.add(r);
        }

        random.forEach(i -> {
            SimpleSpawner spawnDat = (SimpleSpawner) new SimpleSpawner(ARCHANGEL)
                    .setLoc(ANGEL_LOCATION[i])
                    .setAmount(1)
                    .setRespawnDelay(300000);
            _angelSpawns.add(spawnDat);
        });

        LOG.info("BaiumManager: State of Baium is " + state.getState() + '.');
        if (state.getState().equals(State.NOTSPAWN))
            _statueSpawn.doSpawn(true);
        else if (state.getState().equals(State.ALIVE)) {
            state.setState(State.NOTSPAWN);
            state.update();
            _statueSpawn.doSpawn(true);
        } else if (state.getState().equals(State.INTERVAL) || state.getState().equals(State.DEAD))
            setIntervalEndTask();

        LOG.info("BaiumManager: Next spawn date: " + TimeUtils.toSimpleFormat(state.getRespawnDate()));
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (actor.isPlayer() && state != null && state.getState() == State.ALIVE && zone != null && zone.checkIfInZone(actor))
            checkAnnihilated();
        else if (actor.isNpc() && actor.getNpcId() == BAIUM)
            onBaiumDie(actor);
    }

    @Override
    public void onLoad() {
        init();
    }

    @Override
    public void onReload() {
        sleepBaium();
    }

    @Override
    public void onShutdown() {
    }

    // call Arcangels
    public static class CallArchAngel extends RunnableImpl {
        @Override
        public void runImpl() {
            for (SimpleSpawner spawn : _angelSpawns)
                _angels.add(spawn.doSpawn(true));
        }
    }

    public static class CheckLastAttack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (state.getState().equals(State.ALIVE))
                if (_lastAttackTime + FWB_LIMITUNTILSLEEP < System.currentTimeMillis())
                    sleepBaium();
                else
                    _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), 60000);
        }
    }


    // kill pc
    public static class KillPc extends RunnableImpl {
        private final BossInstance _boss;
        private final Player _target;

        KillPc(Player target, BossInstance boss) {
            _target = target;
            _boss = boss;
        }

        @Override
        public void runImpl() {
            Skill skill = SkillTable.INSTANCE.getInfo(4136, 1);
            if (_target != null && skill != null) {
                _boss.setTarget(_target);
                _boss.doCast(skill, _target, false);
            }
        }
    }

    // Move at random on after Baium appears.
    public static class MoveAtRandom extends RunnableImpl {
        private final NpcInstance _npc;
        private final Location _pos;

        MoveAtRandom(NpcInstance npc, Location pos) {
            _npc = npc;
            _pos = pos;
        }

        @Override
        public void runImpl() {
            if (_npc.getAI().getIntention() == AI_INTENTION_ACTIVE)
                _npc.moveToLocation(_pos, 0, false);
        }
    }

    public static class SetMobilised extends RunnableImpl {
        private final BossInstance _boss;

        SetMobilised(BossInstance boss) {
            _boss = boss;
        }

        @Override
        public void runImpl() {
            _boss.stopImmobilized();
        }
    }

    // do social.
    public static class Social extends RunnableImpl {
        private final int _action;
        private final NpcInstance _npc;

        Social(NpcInstance npc, int actionId) {
            _npc = npc;
            _action = actionId;
        }

        @Override
        public void runImpl() {
            SocialAction sa = new SocialAction(_npc.getObjectId(), _action);
            _npc.broadcastPacket(sa);
        }
    }

    public static class onAnnihilated extends RunnableImpl {
        @Override
        public void runImpl() {
            sleepBaium();
        }
    }

    public static class EarthquakeTask extends RunnableImpl {
        private final BossInstance baium;

        EarthquakeTask(BossInstance baium) {
            this.baium = baium;
        }

        @Override
        public void runImpl() {
            Earthquake eq = new Earthquake(baium.getLoc(), 40, 5);
            baium.broadcastPacket(eq);
        }
    }
}