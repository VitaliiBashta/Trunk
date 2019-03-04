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
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.*;
import l2trunk.scripts.bosses.EpicBossState.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AntharasManager extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger _log = LoggerFactory.getLogger(AntharasManager.class);

    // Constants
    private static final int _teleportCubeId = 31859;
    private static final int ANTHARAS_STRONG = 29068;
    private static final int PORTAL_STONE = 3865;
    private static final Location TELEPORT_POSITION = new Location(179892, 114915, -7704);
    private static final Location _teleportCubeLocation = new Location(177615, 114941, -7709);
    private static final Location _antharasLocation = new Location(181911, 114835, -7678, 32542);
    private static final List<NpcInstance> _spawnedMinions = new ArrayList<>();
    private static final int FWA_LIMITUNTILSLEEP = 15 * 60000;
    private static final int FWA_APPTIMEOFANTHARAS = 15 * 60000;
    // Models
    private static BossInstance antharas;
    private static NpcInstance _teleCube;
    // tasks.
    private static ScheduledFuture<?> _monsterSpawnTask;
    private static ScheduledFuture<?> _intervalEndTask;
    private static ScheduledFuture<?> _socialTask;
    private static ScheduledFuture<?> _moveAtRandomTask;
    private static ScheduledFuture<?> _sleepCheckTask;
    private static ScheduledFuture<?> onAnnihilatedTask;
    // Vars
    private static EpicBossState _state;
    private static Zone zone;
    private static long _lastAttackTime = 0;
    private static boolean Dying = false;
    private static boolean _entryLocked = false;

    private static void banishForeigners() {
        getPlayersInside().forEach(Player::teleToClosestTown);
    }

    private synchronized static void checkAnnihilated() {
        if (onAnnihilatedTask == null && getPlayersInside().allMatch(Player::isDead))
            onAnnihilatedTask = ThreadPoolManager.INSTANCE.schedule(AntharasManager::sleep, 5000);
    }

    private static Stream<Player> getPlayersInside() {
        return zone.getInsidePlayers();
    }

    private static long getRespawnInterval() {
        return (long) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (Config.ANTHARAS_DEFAULT_SPAWN_HOURS * 60 * 60000 + Rnd.get(0, Config.ANTHARAS_RANDOM_SPAWN_HOURS * 60 * 60000)));
    }

    public static Zone getZone() {
        return zone;
    }

    private static void onAntharasDie() {
        if (Dying)
            return;

        Dying = true;
        _state.setRespawnDate(getRespawnInterval());
        _state.setState(State.INTERVAL);
        _state.update();

        _entryLocked = false;
        _teleCube = NpcUtils.spawnSingle(_teleportCubeId,_teleportCubeLocation );
        Log.add("Antharas died", "bosses");
    }

    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState().equals(State.ALIVE)) {
            _state.setState(State.NOTSPAWN);
            _state.update();
            return;
        }

        if (!_state.getState().equals(State.INTERVAL)) {
            _state.setRespawnDate(getRespawnInterval());
            _state.setState(State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.INSTANCE.schedule(new IntervalEnd(), _state.getInterval());
    }

    // clean Antharas's lair.
    private static void setUnspawn() {
        // eliminate players.
        banishForeigners();

        if (antharas != null)
            antharas.deleteMe();
        for (NpcInstance npc : _spawnedMinions)
            npc.deleteMe();
        if (_teleCube != null)
            _teleCube.deleteMe();

        _entryLocked = false;

        // not executed tasks is canceled.
        if (_monsterSpawnTask != null) {
            _monsterSpawnTask.cancel(false);
            _monsterSpawnTask = null;
        }
        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_socialTask != null) {
            _socialTask.cancel(false);
            _socialTask = null;
        }
        if (_moveAtRandomTask != null) {
            _moveAtRandomTask.cancel(false);
            _moveAtRandomTask = null;
        }
        if (_sleepCheckTask != null) {
            _sleepCheckTask.cancel(false);
            _sleepCheckTask = null;
        }
        if (onAnnihilatedTask != null) {
            onAnnihilatedTask.cancel(false);
            onAnnihilatedTask = null;
        }
    }

    private static void sleep() {
        setUnspawn();
        if (_state.getState().equals(State.ALIVE)) {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }
    }

    public static void setLastAttackTime() {
        _lastAttackTime = System.currentTimeMillis();
    }

    // setting Antharas spawn task.
    private synchronized static void setAntharasSpawnTask() {
        if (_monsterSpawnTask == null)
            _monsterSpawnTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(1), FWA_APPTIMEOFANTHARAS);
        //_entryLocked = true;
    }

    private static void broadcastScreenMessage(NpcString npcs) {
        getPlayersInside().forEach(p -> p.sendPacket(new ExShowScreenMessage(npcs)));
    }

    public static void addSpawnedMinion(NpcInstance npc) {
        _spawnedMinions.add(npc);
    }

    public static void enterTheLair(Player player) {
        if (player == null)
            return;

        if (getPlayersInside().count() > 200) {
            player.sendMessage("The maximum of 200 players can invade the Antharas Nest");
            return;
        }
        if (_state.getState() != State.NOTSPAWN) {
            player.sendMessage("Antharas is still reborning. You cannot invade the nest now");
            return;
        }
        if (_entryLocked) {
            player.sendMessage("Antharas has already been reborned and is being attacked. The entrance is sealed.");
            return;
        }

        if (player.isDead() || player.isFlying() || player.isCursedWeaponEquipped() || player.getInventory().getCountOf(PORTAL_STONE) < 1) {
            player.sendMessage("You doesn't meet the requirements to enter the nest");
            return;
        }

        player.teleToLocation(TELEPORT_POSITION);
        setAntharasSpawnTask();

    }

    public static EpicBossState getState() {
        return _state;
    }

    @Override
    public void onDeath(Creature self, Creature killer) {
        if (self instanceof Player && _state != null && _state.getState() == State.ALIVE && zone != null && zone.checkIfInZone(self.getX(), self.getY()))
            checkAnnihilated();
        else if (self instanceof NpcInstance && self.getNpcId() == ANTHARAS_STRONG)
            ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(8), 10);
    }

    private void init() {
        _state = new EpicBossState(ANTHARAS_STRONG);
        zone = ReflectionUtils.getZone("[antharas_epic]");

        CharListenerList.addGlobal(this);
        _log.info("AntharasManager: State of Antharas is " + _state.getState() + ".");
        if (!_state.getState().equals(State.NOTSPAWN))
            setIntervalEndTask();

        _log.info("AntharasManager: Next spawn date of Antharas is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    @Override
    public void onLoad() {
        init();
    }

    @Override
    public void onReload() {
        sleep();
    }

    private static class AntharasSpawn extends RunnableImpl {
        private final int _distance = 3000;
        private final List<Player> players = getPlayersInside().collect(Collectors.toList());
        private int taskId;

        AntharasSpawn(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void runImpl() {
            _entryLocked = true;
            switch (taskId) {
                case 1:
                    antharas = (BossInstance) NpcUtils.spawnSingle(ANTHARAS_STRONG,_antharasLocation );
                    antharas.setAggroRange(0);
                    _state.setRespawnDate(getRespawnInterval());
                    _state.setState(State.ALIVE);
                    _state.update();
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(2), 2000);
                    break;
                case 2:
                    // set camera.
                    players.forEach(pc -> {
                                if (pc.getDistance(antharas) <= _distance) {
                                    pc.enterMovieMode();
                                    pc.specialCamera(antharas, 700, 13, -19, 0, 20000, 0, 0, 0, 0);
                                } else
                                    pc.leaveMovieMode();
                            });
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(3), 3000);
                    break;
                case 3:
                    // do social.
                    antharas.broadcastPacket(new SocialAction(antharas.objectId(), 1));

                    // set camera.
                    players.forEach(pc -> {
                        if (pc.getDistance(antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera(antharas, 700, 13, 0, 6000, 20000, 0, 0, 0, 0);
                        } else
                            pc.leaveMovieMode();
                    });
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(4), 10000);
                    break;
                case 4:
                    antharas.broadcastPacket(new SocialAction(antharas.objectId(), 2));
                    // set camera.
                    for (Player pc : players)
                        if (pc.getDistance(antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera(antharas, 3700, 0, -3, 0, 10000, 0, 0, 0, 0);
                        } else
                            pc.leaveMovieMode();
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(5), 200);
                    break;
                case 5:
                    // set camera.
                    for (Player pc : players)
                        if (pc.getDistance(antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera(antharas, 1100, 0, -3, 22000, 30000, 0, 0, 0, 0);
                        } else
                            pc.leaveMovieMode();
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(6), 10800);
                    break;
                case 6:
                    // set camera.
                    for (Player pc : players)
                        if (pc.getDistance(antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera(antharas, 1100, 0, -3, 300, 7000, 0, 0, 0, 0);
                        } else
                            pc.leaveMovieMode();
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(7), 7000);
                    break;
                case 7:
                    // reset camera.
                    for (Player pc : players)
                        pc.leaveMovieMode();

                    broadcastScreenMessage(NpcString.ANTHARAS_YOU_CANNOT_HOPE_TO_DEFEAT_ME);
                    antharas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, antharas.objectId(), antharas.getLoc()));
                    antharas.setAggroRange(antharas.getTemplate().aggroRange);
                    antharas.setRunning();
                    antharas.moveToLocation(new Location(179011, 114871, -7704), 0, false);
                    _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), 600000);
                    break;
                case 8:
                    for (Player pc : players)
                        if (pc.getDistance(antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera(antharas, 1200, 20, -10, 0, 13000, 0, 0, 0, 0);
                        } else
                            pc.leaveMovieMode();
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new AntharasSpawn(9), 13000);
                    break;
                case 9:
                    players.forEach(pc -> {
                        pc.leaveMovieMode();
                        pc.altOnMagicUseTimer(pc, 23312);
                    });
                    broadcastScreenMessage(NpcString.ANTHARAS_THE_EVIL_LAND_DRAGON_ANTHARAS_DEFEATED);
                    onAntharasDie();
                    break;
            }
        }
    }

    private static class CheckLastAttack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_state.getState() == State.ALIVE)
                if (_lastAttackTime + FWA_LIMITUNTILSLEEP < System.currentTimeMillis())
                    sleep();
                else
                    _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), 60000);
        }
    }

    // at end of interval.
    private static class IntervalEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            _state.setState(State.NOTSPAWN);
            _state.update();
            //Earthquake
            Location loc = new Location(185708, 114298, -8221);
            GameObjectsStorage.getAllPlayersStream().forEach(p -> p.broadcastPacket(new Earthquake(loc, 20, 10)));

        }
    }

}