package l2trunk.scripts.bosses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.TimeUtils;
import l2trunk.scripts.bosses.EpicBossState.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class SailrenManager extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger _log = LoggerFactory.getLogger(SailrenManager.class);
    private static final int Sailren = 29065;
    private static final int Velociraptor = 22198;
    private static final int Pterosaur = 22199;
    private static final int Tyrannosaurus = 22217;
    private static final int TeleportCubeId = 31759;
    private static final Location _enter = new Location(27734, -6938, -1982);
    private static final boolean FWS_ENABLESINGLEPLAYER = Boolean.TRUE;
    private static final int FWS_ACTIVITYTIMEOFMOBS = 120 * 60000;
    private static final int FWS_FIXINTERVALOFSAILRENSPAWN = Config.FIXINTERVALOFSAILRENSPAWN_HOUR * 60 * 60000;
    private static final int FWS_RANDOMINTERVALOFSAILRENSPAWN = Config.RANDOMINTERVALOFSAILRENSPAWN * 60 * 60000;
    private static final int FWS_INTERVALOFNEXTMONSTER = 60000;
    private static NpcInstance _velociraptor;
    private static NpcInstance _pterosaur;
    private static NpcInstance _tyranno;
    private static NpcInstance _sailren;
    private static NpcInstance _teleportCube;
    // Tasks.
    private static ScheduledFuture<?> _cubeSpawnTask = null;
    private static ScheduledFuture<?> _monsterSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _activityTimeEndTask = null;
    private static ScheduledFuture<?> _onAnnihilatedTask = null;
    private static EpicBossState _state;
    private static Zone zone;
    private static boolean isAlreadyEnteredOtherParty = false;
    private static boolean Dying = false;

    private static void banishForeigners() {
        getPlayersInside().forEach(Player::teleToClosestTown);
    }

    private synchronized static void checkAnnihilated() {
        if (_onAnnihilatedTask == null && isPlayersAnnihilated())
            _onAnnihilatedTask = ThreadPoolManager.INSTANCE.schedule(SailrenManager::sleep, 5000);
    }

    private static List<Player> getPlayersInside() {
        return zone.getInsidePlayers();
    }

    private static int getRespawnInterval() {
        return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWS_FIXINTERVALOFSAILRENSPAWN + Rnd.get(0, FWS_RANDOMINTERVALOFSAILRENSPAWN)));
    }

    private static boolean isPlayersAnnihilated() {
        return getPlayersInside().stream().allMatch(Player::isDead);

    }

    private static void onSailrenDie() {
        if (Dying)
            return;

        Dying = true;
        _state.setRespawnDate(getRespawnInterval());
        _state.setState(State.INTERVAL);
        _state.update();

        Log.add("Sailren died", "bosses");

        _cubeSpawnTask = ThreadPoolManager.INSTANCE.schedule(() ->
                _teleportCube = spawn(new Location(27734, -6838, -1982, 0), TeleportCubeId), 10000);
    }

    // Start interval.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState().equals(State.ALIVE)) {
            _state.setState(State.NOTSPAWN);
            _state.update();
            return;
        }

        //init state of Sailren lair.
        if (!_state.getState().equals(State.INTERVAL)) {
            _state.setRespawnDate(getRespawnInterval());
            _state.setState(State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }, _state.getInterval());
    }

    private static void setUnspawn() {
        banishForeigners();

        if (_velociraptor != null) {
            if (_velociraptor.getSpawn() != null)
                _velociraptor.getSpawn().stopRespawn();
            _velociraptor.deleteMe();
            _velociraptor = null;
        }
        if (_pterosaur != null) {
            if (_pterosaur.getSpawn() != null)
                _pterosaur.getSpawn().stopRespawn();
            _pterosaur.deleteMe();
            _pterosaur = null;
        }
        if (_tyranno != null) {
            if (_tyranno.getSpawn() != null)
                _tyranno.getSpawn().stopRespawn();
            _tyranno.deleteMe();
            _tyranno = null;
        }
        if (_sailren != null) {
            if (_sailren.getSpawn() != null)
                _sailren.getSpawn().stopRespawn();
            _sailren.deleteMe();
            _sailren = null;
        }
        if (_teleportCube != null) {
            if (_teleportCube.getSpawn() != null)
                _teleportCube.getSpawn().stopRespawn();
            _teleportCube.deleteMe();
            _teleportCube = null;
        }
        if (_cubeSpawnTask != null) {
            _cubeSpawnTask.cancel(false);
            _cubeSpawnTask = null;
        }
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
        if (_activityTimeEndTask != null) {
            _activityTimeEndTask.cancel(false);
            _activityTimeEndTask = null;
        }
        if (_onAnnihilatedTask != null) {
            _onAnnihilatedTask.cancel(false);
            _onAnnihilatedTask = null;
        }
    }

    private static void sleep() {
        setUnspawn();
        if (_state.getState().equals(State.ALIVE)) {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }
    }

    public synchronized static void setSailrenSpawnTask() {
        if (_monsterSpawnTask == null)
            _monsterSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SailrenSpawn(Velociraptor), FWS_INTERVALOFNEXTMONSTER);
    }

    public static int canIntoSailrenLair(Player pc) {
        if (!FWS_ENABLESINGLEPLAYER && pc.getParty() == null)
            return 4;
        else if (isAlreadyEnteredOtherParty)
            return 2;
        else if (_state.getState().equals(State.NOTSPAWN))
            return 0;
        else if (_state.getState().equals(State.ALIVE) || _state.getState().equals(State.DEAD))
            return 1;
        else if (_state.getState().equals(State.INTERVAL))
            return 3;
        else
            return 0;
    }

    public static void entryToSailrenLair(Player pc) {
        if (pc.getParty() == null)
            pc.teleToLocation(Location.findPointToStay(_enter, 80, pc.getGeoIndex()));
        else {
            List<Player> members = new ArrayList<>();
            for (Player mem : pc.getParty().getMembers())
                if (mem != null && !mem.isDead() && mem.isInRange(pc, 1000))
                    members.add(mem);
            members.forEach(mem ->
                mem.teleToLocation(Location.findPointToStay(_enter, 80, mem.getGeoIndex())));
        }
        isAlreadyEnteredOtherParty = true;
    }

    private void init() {
        CharListenerList.addGlobal(this);

        _state = new EpicBossState(Sailren);
        zone = ReflectionUtils.getZone("[sailren_epic]");

        _log.info("SailrenManager: State of Sailren is " + _state.getState() + ".");
        if (!_state.getState().equals(State.NOTSPAWN))
            setIntervalEndTask();

        _log.info("SailrenManager: Next spawn date of Sailren is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
    }

    @Override
    public void onDeath(Creature self, Creature killer) {
        if (self.isPlayer() && _state != null && _state.getState() == State.ALIVE && zone != null && zone.checkIfInZone(self.getX(), self.getY()))
            checkAnnihilated();
        else if (self == _velociraptor) {
            if (_monsterSpawnTask != null)
                _monsterSpawnTask.cancel(false);
            _monsterSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SailrenSpawn(Pterosaur), FWS_INTERVALOFNEXTMONSTER);
        } else if (self == _pterosaur) {
            if (_monsterSpawnTask != null)
                _monsterSpawnTask.cancel(false);
            _monsterSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SailrenSpawn(Tyrannosaurus), FWS_INTERVALOFNEXTMONSTER);
        } else if (self == _tyranno) {
            if (_monsterSpawnTask != null)
                _monsterSpawnTask.cancel(false);
            _monsterSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SailrenSpawn(Sailren), FWS_INTERVALOFNEXTMONSTER);
        } else if (self == _sailren) {
            onSailrenDie();
        }
    }

    @Override
    public void onLoad() {
        init();
    }

    @Override
    public void onReload() {
        sleep();
    }

    @Override
    public void onShutdown() {
    }

    private static class ActivityTimeEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            sleep();
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


    private static class SailrenSpawn extends RunnableImpl {
        private final int npcId;
        private final Location _pos = new Location(27628, -6109, -1982, 44732);

        SailrenSpawn(int npcId) {
            this.npcId = npcId;
        }

        @Override
        public void runImpl() {
            if (_socialTask != null) {
                _socialTask.cancel(false);
                _socialTask = null;
            }

            switch (npcId) {
                case Velociraptor:
                    _velociraptor = spawn(new Location(27852, -5536, -1983, 44732), Velociraptor);
                    ((DefaultAI) _velociraptor.getAI()).addTaskMove(_pos, false);

                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(_velociraptor, 2), 6000);
                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.INSTANCE.schedule(SailrenManager::sleep, FWS_ACTIVITYTIMEOFMOBS);
                    break;
                case Pterosaur:
                    _pterosaur = spawn(new Location(27852, -5536, -1983, 44732), Pterosaur);
                    ((DefaultAI) _pterosaur.getAI()).addTaskMove(_pos, false);
                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(_pterosaur, 2), 6000);
                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.INSTANCE.schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
                    break;
                case Tyrannosaurus:
                    _tyranno = spawn(new Location(27852, -5536, -1983, 44732), Tyrannosaurus);
                    ((DefaultAI) _tyranno.getAI()).addTaskMove(_pos, false);
                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(_tyranno, 2), 6000);
                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.INSTANCE.schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
                    break;
                case Sailren:
                    _sailren = spawn(new Location(27810, -5655, -1983, 44732), Sailren);

                    _state.setRespawnDate(getRespawnInterval() + FWS_ACTIVITYTIMEOFMOBS);
                    _state.setState(State.ALIVE);
                    _state.update();

                    _sailren.setRunning();
                    ((DefaultAI) _sailren.getAI()).addTaskMove(_pos, false);
                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.INSTANCE.schedule(new Social(_sailren, 2), 6000);
                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.INSTANCE.schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
                    break;
            }
        }
    }
}